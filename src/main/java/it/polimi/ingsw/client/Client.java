package it.polimi.ingsw.client;

import it.polimi.ingsw.NotExecutedException;
import it.polimi.ingsw.controller.lobby.rpc.RemoteLobbyController;
import it.polimi.ingsw.InternalError;
import it.polimi.ingsw.rpc.RemoteCommandHandler;
import it.polimi.ingsw.rpc.RequestProcessor;
import it.polimi.ingsw.views.lobby.MultiUserConsoleLobbyView;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Client {
    // A specialized RequestProcessor that can dispatch calls of remoteInvoke
    // to the event loop thread
    private static class DispatchingProcessor extends RequestProcessor {

        public DispatchingProcessor(Socket socket) throws IOException {
            super(socket);
        }

        @Override
        public void addHandler(RemoteCommandHandler handler) {
            try {
                this.call(() -> {
                    super.addHandler(handler);
                    return null;
                });
            } catch (InterruptedException | ExecutionException e) {
                throw new InternalError(e);
            }
        }

        @Override
        public void removeHandler(RemoteCommandHandler handler) {
            try {
                this.call(() -> {
                    super.removeHandler(handler);
                    return null;
                });
            } catch (InterruptedException | ExecutionException e) {
                throw new InternalError(e);
            }
        }

        @Override
        public Serializable remoteInvoke(Serializable command)
                throws NotExecutedException, IOException {
            try {
                return this.call(() -> super.remoteInvoke(command));
            } catch (InterruptedException | ExecutionException e) {
                Throwable cause = e.getCause();
                if (cause instanceof IOException) {
                    throw (IOException) cause;
                }
                if (cause instanceof NotExecutedException) {
                    throw (NotExecutedException) cause;
                }
                throw new InternalError(e);
            }
        }

        private <T> T call(Callable<T> callable)
                throws ExecutionException, InterruptedException {
            CompletableFuture<T> result = new CompletableFuture<>();
            this.invokeAsync(() -> {
                try {
                    result.complete(callable.call());
                }
                catch (Exception e) {
                    result.completeExceptionally(e);
                }
            });
            return result.get();
        }
    }

    private final String ip;
    private final int port;

    public Client(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void run() throws IOException, InterruptedException, NotExecutedException {
        try (Socket socket = new Socket(ip, port);
             RequestProcessor processor = new DispatchingProcessor(socket)) {
            System.out.println("Connection established");
            Thread thread = new Thread(() -> {
                try {
                    processor.runEventLoop();
                    System.out.println("Event loop terminated gracefully");
                } catch (Exception e) {
                    System.out.println("Event loop terminated with exception:");
                    e.printStackTrace();
                }
            });
            thread.start();
            RemoteLobbyController controller = new RemoteLobbyController(processor);
            MultiUserConsoleLobbyView view = new MultiUserConsoleLobbyView(controller);

            view.getUserInputUntilGameStarts();
            System.out.println("Got Game Controller (Not implemented yet)");
            processor.requestStop();
            thread.join();
        }
    }
}
