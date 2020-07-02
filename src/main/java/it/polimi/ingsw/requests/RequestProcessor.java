package it.polimi.ingsw.requests;

import it.polimi.ingsw.NotExecutedException;
import it.polimi.ingsw.InternalError;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

public class RequestProcessor implements AutoCloseable {
    /**
     * A class that signals the end of BlockingQueue
     */
    private static class Poison implements Serializable {
    }

    private final BlockingQueue<Object> received = new LinkedBlockingQueue<>();
    private final Thread receiverThread;
    private final Socket socket;
    private final ObjectOutputStream out;
    private final List<RemoteRequestHandler> handlers = new ArrayList<>();
    private final AtomicReference<Thread> eventThread = new AtomicReference<>();

    private long sequenceNumber = 0;

    public RequestProcessor(Socket socket)
            throws IOException {
        this.receiverThread = new Thread(() -> {
            try {
                ObjectInputStream in =
                        new ObjectInputStream(socket.getInputStream());
                while (true) {
                    // receive objects and put them to the received queue
                    Object next = in.readObject();
                    this.received.add(next);
                    if (next instanceof Poison) {
                        // if received a poison, then stop receiving
                        break;
                    }
                }
            } catch (IOException | ClassNotFoundException ignored) {
                this.received.add(new Poison());
            }
        });
        this.socket = socket;

        // ObjectOutputStream will send a header to the remote ObjectInputStream
        // So we should not create ObjectOutputStream after ObjectInputStream
        // Either create output stream before the input stream, or create them
        // in parallel.
        this.out = new ObjectOutputStream(this.socket.getOutputStream());
        // flush the stream to make sure the header is sent
        this.out.flush();

        this.receiverThread.start();
    }

    /**
     * Close the connection. Should be called only from the event loop thread
     * (after event loop finishes)
     *
     * @throws IOException          if close fails
     */
    @Override
    public void close() throws IOException {
        this.socket.close();
        try {
            this.receiverThread.join();
        }
        catch (InterruptedException e) {
            throw new InternalError(e);
        }
    }

    /**
     * Checks whether the current thread is the event thread
     * i.e. if the current function is invoked by a
     * {@link RemoteRequestHandler}.
     * @return true if current thread is event thread.
     */
    public boolean isOnEventThread() {
        return Thread.currentThread().equals(eventThread.get());
    }

    /**
     * Executes the specified action asynchronously.
     *
     * @param action The action to be executed
     */
    public void invokeAsync(Runnable action) {
        if (this.isOnEventThread()) {
            String errorMessage = "Already on the event loop thread, " +
                    "you shouldn't need to call invokeAsync()";
            throw new InternalError(errorMessage);
        }
        this.received.add(action);
    }

    /**
     * Request the event loop to stop, this can be called from other threads.
     */
    public void requestStop() {
        this.received.add(new Poison());
    }

    /**
     * Add a {@link RemoteRequestHandler} to the request processor.
     * This method should only be called from the event loop thread
     * (through {@link RemoteRequestHandler} or {@link #invokeAsync(Runnable)}).
     */
    public void addHandler(RemoteRequestHandler handler) {
        this.checkThread();
        this.handlers.add(handler);
    }

    /**
     * Remove a {@link RemoteRequestHandler} from the request processor.
     * This method should only be called from the event loop thread
     * (through {@link RemoteRequestHandler} or {@link #invokeAsync(Runnable)}).
     */
    public void removeHandler(RemoteRequestHandler handler) {
        this.checkThread();
        this.handlers.remove(handler);
    }

    /**
     * Make a remote request and waits for its result.
     * This method should only be called from the event loop thread
     * (through {@link RemoteRequestHandler} or {@link #invokeAsync(Runnable)}).
     *
     * @param command The command to be processed remotely.
     * @return The result of this request.
     * @throws NotExecutedException If this request was not executed by the
     *                              remote target.
     * @throws IOException          If failed to send request or receive response.
     */
    public Serializable remoteInvoke(Serializable command)
            throws NotExecutedException, IOException {
        this.checkThread();
        long sequenceNumber = this.sequenceNumber;
        this.sequenceNumber += 1;
        this.writeAndFlush(new Request(sequenceNumber, command));

        while (true) {
            Object input = this.takeNext();
            if (input instanceof Poison) {
                // since the request isn't completed yet,
                // let's treat it just like socket has been closed remotely
                throw new IOException("Poison received");
            }

            if (!(input instanceof Response)) {
                this.processRequest(input);
                continue;
            }

            Response response = (Response) input;
            if (response.getSequenceNumber() != sequenceNumber) {
                throw new InternalError("Sequence number does not match");
            }

            return response.getResult();
        }
    }

    /**
     * Make a remote request, but don't wait for its result, and don't check
     * whether the request has succeeded or failed with any exceptions.
     *
     * @param command The command to be processed remotely.
     */
    public void remoteNotify(Serializable command) {
        Runnable action = () -> {
            try {
                this.writeAndFlush(new Request(command));
            } catch (IOException ignored) {
                // request to stop the connection (by adding the poison)
                // if fails
                this.requestStop();
            }
        };
        if (this.isOnEventThread()) {
            action.run();
        } else {
            this.received.add(action);
        }
    }

    /**
     * Run the event loop until the end, it will block in order to wait for
     * next events
     * @throws IOException if any I/O error occurs
     */
    public void runEventLoop() throws IOException {
        if (!this.eventThread.compareAndSet(null, Thread.currentThread())) {
            throw new InternalError("runEventLoop should only be called once");
        }

        while (true) {
            Object next = this.takeNext();
            if (next instanceof Poison) {
                // try to gracefully stop the connection if possible
                this.writeAndFlush((Poison)next);
                break;
            }
            this.processRequest(next);
        }
    }

    private void checkThread() {
        if (!this.isOnEventThread()) {
            String errorMessage = "Called from non-event-loop thread. " +
                    "Use invokeAsync() instead.";
            throw new InternalError(errorMessage);
        }
    }

    private Object takeNext() {
        try {
            return this.received.take();
        } catch (InterruptedException e) {
            // we treat InterruptedException as an
            // unrecoverable InternalError
            throw new InternalError(e);
        }
    }
    
    private void writeAndFlush(Serializable object) throws IOException {
        this.out.writeObject(object);
        this.out.flush();
    }

    private void processRequest(Object input) throws IOException {
        if (input instanceof Runnable) {
            ((Runnable) input).run();
            return;
        }

        Request request = (Request) input;
        Serializable command = request.getCommand();
        Optional<RemoteRequestHandler> handler = this.handlers.stream()
                .filter(x -> x.isProcessable(command)).findFirst();
        if (handler.isPresent()) {
            if (request.needReply()) {
                Response response;
                try {
                    response = request.replyResult(handler.get().processRequest(command));
                } catch (NotExecutedException e) {
                    response = request.replyError(e);
                }
                this.writeAndFlush(response);
            } else {
                try {
                    Serializable result = handler.get().processRequest(command);
                    if (result != null) {
                        // this request does not need to be replied, yet it
                        // generated a meaningful response, which might be an
                        // error that needs to be fixed.
                        throw new InternalError("Unnecessary response");
                    }
                } catch (NotExecutedException e) {
                    // ignore any NotExecutedException propagated until here
                    // because the remote caller does not care if it has
                    // succeeded or not.
                }
            }
        } else {
            if (request.needReply()) {
                NotExecutedException error =
                        new NotExecutedException("No suitable handlers");
                this.writeAndFlush(request.replyError(error));
            }
        }
    }


}
