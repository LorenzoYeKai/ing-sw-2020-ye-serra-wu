package it.polimi.ingsw.rpc;

import it.polimi.ingsw.controller.NotExecutedException;
import it.polimi.ingsw.models.InternalError;

import java.io.*;
import java.net.Socket;
import java.rmi.Remote;
import java.util.*;

public class RequestProcessor implements AutoCloseable {
    /**
     * @see #checkAndSetCurrentThread()
     */
    private class CurrentThreadResetter implements AutoCloseable {
        @Override
        public void close() {
            synchronized (RequestProcessor.this) {
                RequestProcessor.this.currentThread = null;
            }
        }
    }

    private final Socket socket;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;
    private final List<RemoteCommandHandler> handlers = new ArrayList<>();
    private long sequenceNumber = 0;
    private Thread currentThread = null;


    public RequestProcessor(Socket socket)
            throws IOException {
        this.socket = socket;
        // ObjectOutputStream will send a header to the remote ObjectInputStream
        // So ObjectOutputStream must always be created before ObjectInputStream
        // Otherwise ObjectInputStream will wait for the header forever
        this.out = new ObjectOutputStream(this.socket.getOutputStream());
        // flush the stream to make sure the header is sent
        this.out.flush();
        this.in = new ObjectInputStream(this.socket.getInputStream());
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }

    public void addHandler(RemoteCommandHandler handler) {
        try (CurrentThreadResetter ignored = this.checkAndSetCurrentThread()) {
           this.handlers.add(handler);
        }
    }

    public void removeHandler(RemoteCommandHandler handler) {
        try (CurrentThreadResetter ignored = this.checkAndSetCurrentThread()) {
            this.handlers.remove(handler);
        }
    }

    /**
     * Make a remote request and waits for its result.
     *
     * @param command The command to be processed remotely.
     * @return The result of this request.
     * @throws NotExecutedException If this request was not executed by the
     *                              remote target.
     * @throws IOException          If failed to send request or receive response.
     */
    public Serializable makeRequest(Serializable command)
            throws NotExecutedException, IOException {
        try (CurrentThreadResetter ignored = this.checkAndSetCurrentThread()) {
            return this.makeRequestInternal(command);
        }
    }

    /**
     * Make a remote request, but don't wait for its result, and don't check
     * whether the request has succeeded or failed with any exceptions.
     *
     * @param command The command to be processed remotely.
     * @throws IOException If failed to send request or receive response.
     */
    public void sendMessage(Serializable command) throws IOException {
        try (CurrentThreadResetter ignored = this.checkAndSetCurrentThread()) {
            out.writeObject(new Request(command));
        }
    }

    public void handleNextIncomingRequest() throws IOException {
        try (CurrentThreadResetter ignored = this.checkAndSetCurrentThread()) {
            this.handleNextIncomingRequestInternal();
        }
    }

    /**
     * This class is not suitable for multithreaded calls
     * (Especially with the remote call-chain inside while(true) loops)
     * Mark methods as `synchronized` is not suitable either because it might
     * cause deadlocks.
     * So we use this method to check if this class's public methods are being
     * called from multiple threads at the same time, and throw an error if
     * that's the case.
     *
     * @return A {@link CurrentThreadResetter} which can be used to "release"
     * the {@link #currentThread} when everything finishes.
     */
    private synchronized CurrentThreadResetter checkAndSetCurrentThread() {
        if (this.currentThread == null) {
            // if nobody is using this class, then set the current thread
            this.currentThread = Thread.currentThread();
            // returns an AutoClosable which can be used to "release"
            // the current thread when the public method finishes.
            return new CurrentThreadResetter();
        }
        // calling from the same thread is fine
        if (this.currentThread.equals(Thread.currentThread())) {
            // we don't need to "release" the current thread because
            // there must be another call which will release it.
            return null;
        }
        throw new InternalError("Called from multiple threads");
    }

    private Serializable makeRequestInternal(Serializable command)
            throws NotExecutedException, IOException {
        long sequenceNumber = this.sequenceNumber;
        this.sequenceNumber += 1;
        this.out.writeObject(new Request(sequenceNumber, command));

        while (true) {
            Object input;
            try {
                input = this.in.readObject();
            } catch (ClassNotFoundException e) {
                // we treat ClassNotFoundException as an
                // unrecoverable InternalError
                throw new InternalError(e);
            }

            if (input instanceof Request) {
                this.processRemoteRequest((Request) input);
                continue;
            }

            Response response = (Response) input;
            if (response.getSequenceNumber() != sequenceNumber) {
                throw new InternalError("Sequence number does not match");
            }

            return response.getResult();
        }
    }

    private void handleNextIncomingRequestInternal() throws IOException {
        Request request;
        try {
            request = (Request) this.in.readObject();
        } catch (ClassNotFoundException e) {
            // we treat ClassNotFoundException as an
            // unrecoverable InternalError
            throw new InternalError(e);
        }

        this.processRemoteRequest(request);
    }

    private void processRemoteRequest(Request request) throws IOException {
        Serializable command = request.getCommand();
        Optional<RemoteCommandHandler> handler = this.handlers.stream()
                .filter(x -> x.isProcessable(command)).findFirst();
        if (handler.isPresent()) {
            if (request.needReply()) {
                Response response;
                try {
                    response = request.replyResult(handler.get().processCommand(command));
                } catch (NotExecutedException | RuntimeException e) {
                    response = request.replyError(e);
                }
                this.out.writeObject(response);
            } else {
                try {
                    Serializable result = handler.get().processCommand(command);
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
                Exception error = new NotExecutedException("No suitable handlers");
                this.out.writeObject(request.replyError(error));
            }
        }
    }


}
