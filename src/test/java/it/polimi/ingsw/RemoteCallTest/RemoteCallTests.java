package it.polimi.ingsw.RemoteCallTest;

import it.polimi.ingsw.NotExecutedException;
import it.polimi.ingsw.requests.RemoteRequestHandler;
import it.polimi.ingsw.requests.RequestProcessor;

import java.io.IOException;
import java.io.Serializable;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Function;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;


@Timeout(1)
public class RemoteCallTests implements AutoCloseable {

    private static class Helpers {


        static <T> CompletableFuture<T> runAsync(Callable<T> supplier) {
            CompletableFuture<T> future = new CompletableFuture<>();
            new Thread(() -> {
                try {
                    future.complete(supplier.call());
                } catch (Throwable exception) {
                    future.completeExceptionally(exception);
                }
            }).start();
            return future;
        }

        static CompletableFuture<Void> assertNotThrowsAsync(Executable executable) {
            return Helpers.runAsync(() -> {
                Assertions.assertDoesNotThrow(executable);
                return null;
            });
        }

        static <T> RemoteRequestHandler buildHandler(Class<T> type,
                                                     Function<T, Serializable> handler) {
            return new RemoteRequestHandler() {
                @Override
                public boolean isProcessable(Object input) {
                    return type.isInstance(input);
                }

                @Override
                public Serializable processRequest(Object request) {
                    return handler.apply(type.cast(request));
                }
            };
        }
    }

    private RequestProcessor alice;
    private RequestProcessor reimu;


    @Timeout(1)
    @BeforeEach
    void init() throws Exception {
        // setup connection
        final int port = 12345;
        try (ServerSocket server = new ServerSocket(port)) {
            Future<RequestProcessor> futureAlice = Helpers.runAsync(() ->
            {
                Socket socket = new Socket(Inet4Address.getLocalHost(), port);
                return new RequestProcessor(socket) {
                    @Override
                    public String toString() {
                        return "Alice";
                    }
                };
            });

            Future<RequestProcessor> futureReimu = Helpers.runAsync(() ->
            {
                Socket socket = server.accept();
                return new RequestProcessor(socket) {
                    @Override
                    public String toString() {
                        return "Reimu";
                    }
                };
            });

            this.alice = futureAlice.get();
            this.reimu = futureReimu.get();
        }
    }

    @Timeout(1)
    @AfterEach
    @Override
    public void close() throws IOException, InterruptedException {
        // release resources
        if (this.reimu != null) {
            this.reimu.close();
        }
        if (this.alice != null) {
            this.alice.close();
        }
    }

    @Test
    @DisplayName("Test what happens when there are no handlers for the request")
    public void requestNoHandlerTest() throws ExecutionException, InterruptedException {
        // remote handler should not fail
        Future<Void> future = Helpers.assertNotThrowsAsync(() ->
                this.reimu.runEventLoop()
        );
        // this remote request should fail because there aren't any handlers
        // on another side
        this.alice.invokeAsync(() -> {
            Assertions.assertThrows(NotExecutedException.class, () ->
                    this.alice.remoteInvoke("Hello")
            );
            this.alice.requestStop();
        });
        Assertions.assertDoesNotThrow(() -> this.alice.runEventLoop());
        future.get();
    }

    @Test
    @DisplayName("Test what happens when there are no handlers for the message")
    public void messageNoHandlerTest() throws ExecutionException, InterruptedException {
        // remote handlers should not fail
        Future<Void> future = Helpers.assertNotThrowsAsync(() ->
                this.reimu.runEventLoop()
        );
        // this should not fail because with sendMessage we don't care if it
        // succeeded on the remote side or not
        this.alice.invokeAsync(() -> Assertions.assertDoesNotThrow(() -> {
            this.alice.remoteNotify(null);
            this.alice.requestStop();
        }));
        Assertions.assertDoesNotThrow(() -> this.alice.runEventLoop());
        future.get();
    }

    @Test
    @DisplayName("Test a simple request handler")
    public void simpleHandlerTest() throws ExecutionException, InterruptedException {
        // creates a remote handler which multiplies the input by 3
        RemoteRequestHandler handler =
                Helpers.buildHandler(Integer.class, i -> i * 3);
        this.reimu.invokeAsync(() -> this.reimu.addHandler(handler));
        Future<Void> future = Helpers.assertNotThrowsAsync(() ->
                this.reimu.runEventLoop()
        );
        this.alice.invokeAsync(() -> Assertions.assertDoesNotThrow(() -> {
            int result = (int) this.alice.remoteInvoke(10);
            // assert 10 * 3 = 30
            Assertions.assertEquals(result, 30);
            this.alice.requestStop();
        }));
        Assertions.assertDoesNotThrow(() -> this.alice.runEventLoop());
        future.get();
    }

    @Test
    @DisplayName("Test complex call chain")
    public void callChainTest() throws ExecutionException, InterruptedException {
        RemoteRequestHandler aliceHandler = RemoteCallTests.getQuickSorter(this.alice);
        RemoteRequestHandler reimuHandler = RemoteCallTests.getQuickSorter(this.reimu);

        this.alice.invokeAsync(() -> this.alice.addHandler(aliceHandler));
        this.reimu.invokeAsync(() -> this.reimu.addHandler(reimuHandler));

        Future<Void> future = Helpers.assertNotThrowsAsync(() ->
                this.reimu.runEventLoop()
        );
        ArrayList<Integer> list = new ArrayList<>(List.of(1, 9, 4, 8, 0, 2, 5, 7, 3, 6));
        ArrayList<Integer> controlList = new ArrayList<>(list);
        controlList.sort(Comparator.naturalOrder());
        CompletableFuture<Serializable> testList = new CompletableFuture<>();
        this.alice.invokeAsync(() -> Assertions.assertDoesNotThrow(() -> {
            testList.complete(this.alice.remoteInvoke(list));
            this.alice.requestStop();
        }));

        Assertions.assertDoesNotThrow(() -> this.alice.runEventLoop());

        Assertions.assertEquals(controlList, testList.get());
        Assertions.assertNotEquals(controlList, list);

        future.get();
    }

    // a quicksort that involves two peers
    private static RemoteRequestHandler getQuickSorter(RequestProcessor processor) {
        return Helpers.buildHandler(ArrayList.class, l -> {
            // noinspection unchecked
            ArrayList<Integer> list = (ArrayList<Integer>) l;
            if (list.size() <= 1) {
                return list;
            }
            int pivot = list.get(0);
            ArrayList<Integer> less = new ArrayList<>();
            ArrayList<Integer> notLess = new ArrayList<>();
            for (int x : list.subList(1, list.size())) {
                if (x < pivot) {
                    less.add(x);
                } else {
                    notLess.add(x);
                }
            }

            // let the other peer do the quicksort
            ArrayList<Integer> x = Assertions.assertDoesNotThrow(() -> {
                // noinspection unchecked
                return (ArrayList<Integer>) processor.remoteInvoke(less);
            });
            // let the other peer do the quicksort
            ArrayList<Integer> y = Assertions.assertDoesNotThrow(() -> {
                // noinspection unchecked
                return (ArrayList<Integer>) processor.remoteInvoke(notLess);
            });
            x.add(pivot);
            x.addAll(y);
            return x;
        });
    }
}
