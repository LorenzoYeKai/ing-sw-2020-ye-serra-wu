package it.polimi.ingsw.client;

import it.polimi.ingsw.NotExecutedException;
import it.polimi.ingsw.controller.game.GameController;
import it.polimi.ingsw.controller.lobby.remote.ClientLobbyController;
import it.polimi.ingsw.InternalError;
import it.polimi.ingsw.models.game.GameStatus;
import it.polimi.ingsw.models.game.gods.God;
import it.polimi.ingsw.models.game.gods.GodType;
import it.polimi.ingsw.requests.RequestProcessor;
import it.polimi.ingsw.views.game.ConsoleGameView;
import it.polimi.ingsw.views.game.GameView;
import it.polimi.ingsw.views.lobby.ConsoleLobbyView;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Client implements AutoCloseable {
    private final Socket socket;
    private final RequestProcessor processor;
    private final Thread eventThread;

    public Client(String ip, int port) throws IOException {
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

    public void run() throws NotExecutedException, IOException {

        GameController gameCon;
        Scanner input = new Scanner(System.in);
        System.out.println("Type your username: ");
        String userName = input.nextLine();
        ClientLobbyController controller = new ClientLobbyController(processor);
        CompletableFuture<GameController> futureGame = new CompletableFuture<>();
        ConsoleLobbyView view = this.dispatch(() ->
                new ConsoleLobbyView(userName,
                        controller, System.out, futureGame::complete)
        );
        while (!futureGame.isDone()) {
            this.dispatch(() -> {
                view.displaySummary();
                view.displayInputHint();
                return null;
            });

            String line = input.nextLine().strip();
            if (line.isEmpty()) {
                continue;
            }
            String[] splitted = line.split("\\s+", 2);
            if (splitted.length != 2) {
                System.out.println("Wrong input, try again");
                continue;
            }

            this.dispatch(() -> {
                try {
                    view.executeInput(splitted[0], splitted[1]);
                } catch (NotExecutedException e) {
                    System.out.println(e.getMessage());
                }
                return null;
            });
        }


        ConsoleGameView gameView;
        try {
            gameView = futureGame.thenApply(gameController ->
                    this.dispatch(() ->
                            new ConsoleGameView(view.getUserName(), view.getRoomPlayers(),
                                    gameController, System.out)
                    )
            ).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new InternalError(e);
        }
        while (true) {
            this.dispatch(() -> {
                gameView.showHelp();
                return null;
            });
            String line = input.nextLine();
            this.dispatch(() -> {
                try {
                    gameView.executeAction(line);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                return null;
            });
        }



        /*this.processor.requestStop();
        try {
            this.eventThread.join();
        } catch (InterruptedException e) {
            throw new InternalError(e);
        }*/
    }
}

