package it.polimi.ingsw.GUI;

import it.polimi.ingsw.InternalError;
import it.polimi.ingsw.NotExecutedException;
import it.polimi.ingsw.controller.game.GameController;
import it.polimi.ingsw.controller.lobby.remote.ClientLobbyController;
import it.polimi.ingsw.requests.RequestProcessor;
import it.polimi.ingsw.views.game.GUIGameView;
import it.polimi.ingsw.views.lobby.GUILobbyView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class GUIClient implements AutoCloseable {
    private final Socket socket;
    private final RequestProcessor processor;
    private final Thread eventThread;

    public GUIClient(String ip, int port) throws IOException {
        this.socket = new Socket(ip, port);
        this.processor = new RequestProcessor(this.socket);
        this.eventThread = new Thread(() -> {
            try {
                this.processor.runEventLoop();
                System.out.println("Event loop stopped");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        this.eventThread.start();
        System.out.println("Connection established");
    }

    @Override
    public void close() throws IOException {
        this.processor.close();
        this.socket.close();
    }

    /**
     * Dispatch action to the event loop thread, and return the result
     *
     * @param callable the action to be run
     * @param <T>      the return type of the action
     * @return the result of action
     */
    public <T> T dispatch(Callable<T> callable) {
        CompletableFuture<T> result = new CompletableFuture<>();
        if (!this.eventThread.isAlive()) {
            throw new InternalError("Cannot complete because event thread is died");
        }
        this.processor.invokeAsync(() -> {
            try {
                result.complete(callable.call());
            } catch (Exception exception) {
                result.completeExceptionally(exception);
            }
        });
        try {
            return result.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new InternalError(e);
        }
    }

    public void run(String userName, Stage window) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/lobby.fxml"));
        Parent lobby = loader.load();

        //access LobbyGUIController and passing the username

        CompletableFuture<GameController> futureGame = new CompletableFuture<>();

        LobbyGUIController lobbyController = loader.getController();
        lobbyController.initData(userName, this, futureGame);

        ClientLobbyController controller = new ClientLobbyController(processor);


        GUILobbyView view = this.dispatch(() ->
                new GUILobbyView(userName,
                        controller, lobbyController, futureGame::complete)
        );

        Scene lobbyScene = new Scene(lobby);

        window.setScene(lobbyScene);
        window.show();
    }

    public void stopProcessor(){
        System.out.println("Got game controller (Not implemented yet)");
        this.processor.requestStop();
        try {
            this.eventThread.join();
        } catch (InterruptedException e) {
            throw new InternalError(e);
        }
    }

    public void viewInputExec(GUILobbyView view, String command, String data){
        this.dispatch(() -> {
            try {
                view.executeInput(command, data);
            } catch (NotExecutedException e) {
                System.out.println(e.getMessage());
            }
            return null;
        });
    }

    public void gameViewInputExec(GUIGameView gameView, String line){
        this.dispatch(() -> {
            try {
                gameView.executeAction(line);
            } catch (NotExecutedException e) {
                System.out.println(e.getMessage());
            }
            return null;
        });
    }

}
