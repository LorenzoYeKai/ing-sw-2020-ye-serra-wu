package it.polimi.ingsw.GUI;

import it.polimi.ingsw.InternalError;
import it.polimi.ingsw.NotExecutedException;
import it.polimi.ingsw.controller.game.GameController;
import it.polimi.ingsw.controller.game.WorkerActionType;
import it.polimi.ingsw.models.game.GameStatus;
import it.polimi.ingsw.models.game.Space;
import it.polimi.ingsw.models.game.Vector2;
import it.polimi.ingsw.models.game.Worker;
import it.polimi.ingsw.models.game.gods.GodType;
import it.polimi.ingsw.views.game.GUIGameView;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CountDownLatch;

public class GameGUIController implements Initializable{


    private Stage stage;

    private String username;

    private int numberOfPlayers;

    private List<String> listOfPlayers;

    private List<GodType> availableGodsChoseByTheChallenger;

    private List<GodType> availableGods;

    private String firstPlayerName;

    private GameController controller;

    private GUIGameView gameView;

    private GodType chosenGod;

    private GUIClient client;

    private int selectedX;

    private int selectedY;

    private boolean isYourTurn;

    private int selectedWorkerIndex;

    private ImageView workerOne;

    private ImageView workerTwo;

    private ImageView opponentOneWorkerOne;

    private ImageView opponentOneWorkerTwo;

    private ImageView opponentTwoWorkerOne;

    private ImageView opponentTwoWorkerTwo;

    private workerColor color;

    private workerColor opponentOneColor;

    private workerColor opponentTwoColor;

    private Map<WorkerActionType, List<Vector2>> workerOnePossibleActions;

    private boolean utilityBoolean;

    private Map<WorkerActionType, List<Vector2>> workerTwoPossibleActions;

    private WorkerActionType selectedAction;

    public Button lowResButton;

    public Button hdButton;

    public Button fullHdButton;

    public Button fullScreenButton;

    public GridPane boardGrid;

    public BorderPane primaryLayout;

    public VBox leftArea;

    public VBox rightArea;

    public HBox topArea;

    public HBox bottomArea;

    public ImageView yourGod;

    public ImageView opponentOneGod;

    public ImageView opponentTwoGod;

    public Label opponentOne;

    public Label opponentTwo;

    public Label you;

    public Button moveButton;

    public Button buildButton;

    public Button buildDomeButton;

    public Button undoButton;

    public Button endTurnButton;

    public Label messageLabel;

    public AnchorPane background;

    private int utilityCounter;




    public void init(Stage stage, int numberOfPlayers, List<String> listOfPlayers, GameController controller, boolean isChallenger, String username, GUIClient client) throws NotExecutedException, IOException {
        this.stage = stage;
        this.numberOfPlayers = numberOfPlayers;
        this.availableGodsChoseByTheChallenger = new ArrayList<>();
        this.availableGods = new ArrayList<>();
        this.listOfPlayers = listOfPlayers;
        this.controller = controller;
        this.username = username;
        this.chosenGod = null;
        this.client = client;
        this.selectedX = -1;
        this.selectedY = -1;
        this.utilityCounter = 0;
        this.selectedWorkerIndex = -1;
        this.messageLabel.setText("A game has started!");
        this.isYourTurn = false;
        this.utilityBoolean = false;
        this.workerOnePossibleActions = new HashMap<>();
        this.workerTwoPossibleActions = new HashMap<>();
        this.gameView = new GUIGameView(username, listOfPlayers, controller, this);
        client.gameViewInputExec(gameView, "join");
        System.out.println("Number of players: " + numberOfPlayers);
        if(numberOfPlayers == 2){
            System.out.println("removing opponentOne");
            leftArea.getChildren().remove(opponentOneGod);
            leftArea.getChildren().remove(opponentOne);
        }
        if(isChallenger) {

            client.gameViewInputExec(gameView, "setup");
            PauseTransition delay = new PauseTransition(Duration.seconds(0.5));
            delay.setOnFinished(e -> chooseAvailableGod());
            delay.play();
        }
        if(listOfPlayers.indexOf(username) == 0){
            this.color = workerColor.RED;
            this.opponentOneColor = workerColor.GREEN;
            this.opponentTwoColor = workerColor.BLUE;
        }
        if(listOfPlayers.indexOf(username) == 1){
            this.color = workerColor.BLUE;
            this.opponentOneColor = workerColor.GREEN;
            this.opponentTwoColor = workerColor.RED;
        }
        if(listOfPlayers.indexOf(username) == 2){
            this.color = workerColor.GREEN;
            this.opponentOneColor = workerColor.RED;
            this.opponentTwoColor = workerColor.GREEN;
        }
        this.workerOne = new ImageView(new Image((getClass().getResource("/images/" + color.toString() + ".png")).toString()));
        workerOne.setFitHeight(50);
        workerOne.setFitWidth(50);
        this.workerTwo = new ImageView(new Image((getClass().getResource("/images/" + color.toString() + ".png")).toString()));
        workerTwo.setFitHeight(50);
        workerTwo.setFitWidth(50);
        this.opponentOneWorkerOne = new ImageView(new Image((getClass().getResource("/images/" + opponentOneColor.toString() + ".png")).toString()));
        opponentOneWorkerOne.setFitHeight(50);
        opponentOneWorkerOne.setFitWidth(50);
        this.opponentOneWorkerTwo = new ImageView(new Image((getClass().getResource("/images/" + opponentOneColor.toString() + ".png")).toString()));
        opponentOneWorkerTwo.setFitHeight(50);
        opponentOneWorkerTwo.setFitWidth(50);
        this.opponentTwoWorkerOne = new ImageView(new Image((getClass().getResource("/images/" + opponentTwoColor.toString() + ".png")).toString()));
        opponentTwoWorkerOne.setFitHeight(50);
        opponentTwoWorkerOne.setFitWidth(50);
        this.opponentTwoWorkerTwo = new ImageView(new Image((getClass().getResource("/images/" + opponentTwoColor.toString() + ".png")).toString()));
        opponentTwoWorkerTwo.setFitHeight(50);
        opponentTwoWorkerTwo.setFitWidth(50);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        bottomArea.setPrefHeight(80);
        topArea.setPrefHeight(80);
        leftArea.setPrefWidth(350);
        rightArea.setPrefWidth(350);
        yourGod.setFitWidth(150);
        yourGod.setFitHeight(0);
        opponentOneGod.setFitWidth(75);
        opponentOneGod.setFitHeight(0);
        opponentTwoGod.setFitWidth(75);
        opponentTwoGod.setFitHeight(0);
        moveButton.setDisable(true);
        buildButton.setDisable(true);
        buildDomeButton.setDisable(true);
        undoButton.setDisable(true);

    }

    public void setLowRes(){
        stage.setWidth(640);
        stage.setHeight(480);
        bottomArea.setPrefHeight(54);
        topArea.setPrefHeight(54);
        leftArea.setPrefWidth(175);
        rightArea.setPrefWidth(175);
        yourGod.setFitWidth(100);
        yourGod.setFitHeight(0);
        opponentOneGod.setFitWidth(50);
        opponentOneGod.setFitHeight(0);
        opponentTwoGod.setFitWidth(50);
        opponentTwoGod.setFitHeight(0);
    }

    public void setHd(){
        stage.setWidth(1280);
        stage.setHeight(720);
        bottomArea.setPrefHeight(80);
        topArea.setPrefHeight(80);
        leftArea.setPrefWidth(350);
        rightArea.setPrefWidth(350);
        yourGod.setFitWidth(150);
        yourGod.setFitHeight(0);
        opponentOneGod.setFitWidth(75);
        opponentOneGod.setFitHeight(0);
        opponentTwoGod.setFitWidth(75);
        opponentTwoGod.setFitHeight(0);
    }

    public void setFullHd(){
        stage.setWidth(1920);
        stage.setHeight(1080);
        bottomArea.setPrefHeight(120);
        topArea.setPrefHeight(120);
        leftArea.setPrefWidth(540);
        rightArea.setPrefWidth(540);
        yourGod.setFitWidth(250);
        yourGod.setFitHeight(0);
        opponentOneGod.setFitWidth(125);
        opponentOneGod.setFitHeight(0);
        opponentTwoGod.setFitWidth(125);
        opponentTwoGod.setFitHeight(0);
    }

    public void setFullScreen(){
        if(!stage.isFullScreen()) {
            stage.setFullScreen(true);
            bottomArea.setPrefHeight(120);
            topArea.setPrefHeight(120);
            leftArea.setPrefWidth(540);
            rightArea.setPrefWidth(540);
            yourGod.setFitWidth(250);
            yourGod.setFitHeight(0);
            opponentOneGod.setFitWidth(125);
            opponentOneGod.setFitHeight(0);
            opponentTwoGod.setFitWidth(125);
            opponentTwoGod.setFitHeight(0);
            fullScreenButton.setText("Exit FullScreen");
        }
        else{
            stage.setFullScreen(false);
            stage.setWidth(1280);
            stage.setHeight(720);
            bottomArea.setPrefHeight(80);
            topArea.setPrefHeight(80);
            leftArea.setPrefWidth(350);
            rightArea.setPrefWidth(350);
            yourGod.setFitWidth(150);
            yourGod.setFitHeight(0);
            opponentOneGod.setFitWidth(75);
            opponentOneGod.setFitHeight(0);
            opponentTwoGod.setFitWidth(75);
            opponentTwoGod.setFitHeight(0);
            fullScreenButton.setText("FullScreen");
        }

    }

    private void chooseAvailableGod(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/chooseAvailableGod.fxml"));
        try {
            Parent chooseGodLoader = loader.load();
            ChooseAvailableGodController chooseGodController = loader.getController();
            chooseGodController.initData(numberOfPlayers, this);
            Scene chooseGodScene = new Scene(chooseGodLoader);
            Stage chooseGodWindow = new Stage();
            chooseGodWindow.initModality(Modality.APPLICATION_MODAL);
            chooseGodWindow.setScene(chooseGodScene);
            chooseGodWindow.show();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public void setAvailableGodsChoseByTheChallenger(List<GodType> availableGodsChoseByTheChallenger){
        this.availableGodsChoseByTheChallenger = availableGodsChoseByTheChallenger;
    }

    public void setFirstPlayerName(String firstPlayerName){
        this.firstPlayerName = firstPlayerName;
    }




    public void initCurrentTurn(GameStatus currentStatus){
        this.utilityCounter = 0;
        undoButton.setDisable(true);
        System.out.println("Current status: " + currentStatus.toString());
        switch (currentStatus) {
            case CHOOSING_GODS -> chooseGod(this);
            case PLACING -> yourTurnMessage(GameStatus.PLACING, "Place your workers and then click End Turn");
            case PLAYING -> {
                yourTurnMessage(GameStatus.PLAYING, "Select a worker and make a move");
                workerAutoSelect();
            }

            //case ENDED -> ;*/
            default -> throw new InternalError("Not implemented yet");
        }
    }

    private void workerAutoSelect() {
        Service<Void> service = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        //Background work
                        final CountDownLatch latch = new CountDownLatch(1);
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    selectedWorkerIndex = 0;
                                    client.gameViewInputExec(gameView, "select");
                                    client.gameViewInputExec(gameView, "validate0");
                                    selectedWorkerIndex = 1;
                                    client.gameViewInputExec(gameView, "select");
                                    client.gameViewInputExec(gameView, "validate1");
                                } catch (IndexOutOfBoundsException e) {
                                    e.printStackTrace();
                                } finally{
                                    latch.countDown();
                                }
                            }
                        });
                        latch.await();
                        //Keep with the background work
                        return null;
                    }
                };
            }
        };
        service.start();

    }


    private void chooseGod(GameGUIController gameGUIController){
        Service<Void> service = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        //Background work
                        final CountDownLatch latch = new CountDownLatch(1);
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/chooseGod.fxml"));
                                    try {
                                        Parent chooseGodLoader = loader.load();
                                        ChooseGodController chooseGodController = loader.getController();
                                        chooseGodController.initData(numberOfPlayers, gameGUIController, availableGods);
                                        Scene chooseGodScene = new Scene(chooseGodLoader);
                                        Stage chooseGodWindow = new Stage();
                                        chooseGodWindow.initModality(Modality.APPLICATION_MODAL);
                                        chooseGodWindow.setScene(chooseGodScene);
                                        chooseGodWindow.show();
                                    } catch (IOException e) {
                                        System.err.println(e.getMessage());
                                    }
                                } catch (IndexOutOfBoundsException e) {
                                    e.printStackTrace();
                                } finally{
                                    latch.countDown();
                                }
                            }
                        });
                        latch.await();
                        //Keep with the background work
                        return null;
                    }
                };
            }
        };
        service.start();

    }


    public void notYourTurnMessage(GameStatus status, String currentPlayer){
        Service<Void> service = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        //Background work
                        final CountDownLatch latch = new CountDownLatch(1);
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    isYourTurn = false;
                                    messageLabel.setText("It's " + currentPlayer + "'s turn... " + status.toString() + "...");
                                } catch (IndexOutOfBoundsException e) {
                                    e.printStackTrace();
                                } finally{
                                    latch.countDown();
                                }
                            }
                        });
                        latch.await();
                        //Keep with the background work
                        return null;
                    }
                };
            }
        };
        service.start();

    }

    private void yourTurnMessage(GameStatus status, String message){
        Service<Void> service = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        //Background work
                        final CountDownLatch latch = new CountDownLatch(1);
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    isYourTurn = true;
                                    messageLabel.setText("It's your turn... " + message + "...");
                                } catch (IndexOutOfBoundsException e) {
                                    e.printStackTrace();
                                } finally{
                                    latch.countDown();
                                }
                            }
                        });
                        latch.await();
                        //Keep with the background work
                        return null;
                    }
                };
            }
        };
        service.start();
    }

    public void sendChallengerChoices() {
        System.out.println("SelectedGods: ");
        this.availableGodsChoseByTheChallenger.forEach(System.out::println);
        System.out.println(this.firstPlayerName + " -> " + listOfPlayers.indexOf(firstPlayerName));
        client.gameViewInputExec(gameView, "challengerChoice");
    }

    public void sendChosenGod(boolean nextTurn){
        client.gameViewInputExec(this.gameView, "god");
        if(nextTurn){
            client.gameViewInputExec(this.gameView, "placing");
        }
        client.gameViewInputExec(this.gameView, "end");
    }

    public void placeWorker(){
        selectedWorkerIndex = utilityCounter - 1;
        client.gameViewInputExec(gameView, "select");
        client.gameViewInputExec(gameView, "place");
        if(utilityCounter == 2){
            if(areAllWorkerPlaced()){
                client.gameViewInputExec(gameView, "play");
            }

        }
        selectedY = -1;
        selectedX = -1;
    }




    public void addAvailableGods(Collection<GodType> gods){
        if(gods.size() == numberOfPlayers) {
            this.availableGods.addAll(gods);
        }
    }


    public List<String> getListOfPlayers(){
        return this.listOfPlayers;
    }

    public List<GodType> getAvailableGodsChoseByTheChallenger(){
        return  this.availableGodsChoseByTheChallenger;
    }


    public String getFirstPlayerName() {
        return firstPlayerName;
    }

    public void setChosenGod(GodType god){
        this.chosenGod = god;
    }

    public GodType getChosenGod(){
        return this.chosenGod;
    }

    public int getSelectedX(){
        return this.selectedX;
    }

    public int getSelectedY(){
        return this.selectedY;
    }

    public int getSelectedWorkerIndex(){
        return this.selectedWorkerIndex;
    }

    public Map<WorkerActionType, List<Vector2>> getWorkerOnePossibleActions() {
        return workerOnePossibleActions;
    }

    public void setWorkerOnePossibleActions(Map<WorkerActionType, List<Vector2>> workerOnePossibleActions) {
        this.workerOnePossibleActions = workerOnePossibleActions;
    }

    public Map<WorkerActionType, List<Vector2>> getWorkerTwoPossibleActions() {
        return workerTwoPossibleActions;
    }

    public void setWorkerTwoPossibleActions(Map<WorkerActionType, List<Vector2>> workerTwoPossibleActions) {
        this.workerTwoPossibleActions = workerTwoPossibleActions;
    }

    public void displayOpponentsAvailableGods(String playerName, GodType god){
        Service<Void> service = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        //Background work
                        final CountDownLatch latch = new CountDownLatch(1);
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    if(playerName.equals(username)){
                                        yourGod.setImage(new Image((getClass().getResource("/images/" + god.toString() + ".png")).toString()));
                                    }
                                    else{
                                        if(opponentTwo.getText().equals("Label")){
                                            opponentTwoGod.setImage(new Image((getClass().getResource("/images/" + god.toString() + ".png")).toString()));
                                            opponentTwo.setText(playerName);
                                        }
                                        else if(leftArea.getChildren().contains(opponentOneGod) && leftArea.getChildren().contains(opponentOne)){
                                            opponentOneGod.setImage(new Image((getClass().getResource("/images/" + god.toString() + ".png")).toString()));
                                            opponentOne.setText(playerName);
                                        }
                                    }
                                    availableGods.remove(god);
                                } catch (IndexOutOfBoundsException e) {
                                    e.printStackTrace();
                                } finally{
                                    latch.countDown();
                                }
                            }
                        });
                        latch.await();
                        //Keep with the background work
                        return null;
                    }
                };
            }
        };
        service.start();

    }




    public void move(ActionEvent event) {
        if(selectedWorkerIndex == 0){
            workerOnePossibleActions.get(WorkerActionType.MOVE).forEach(this::lightUpSpaces);
        }
        if(selectedWorkerIndex == 1){
            workerTwoPossibleActions.get(WorkerActionType.MOVE).forEach(this::lightUpSpaces);
        }
        selectedAction = WorkerActionType.MOVE;
        moveButton.setDisable(true);
    }

    public void build(ActionEvent event) {
        if(selectedWorkerIndex == 0){
            workerOnePossibleActions.get(WorkerActionType.BUILD).forEach(this::lightUpSpaces);
        }
        if(selectedWorkerIndex == 1){
            workerTwoPossibleActions.get(WorkerActionType.BUILD).forEach(this::lightUpSpaces);
        }
        selectedAction = WorkerActionType.BUILD;
        buildButton.setDisable(true);
    }

    public void buildDome(ActionEvent event) {
        if(selectedWorkerIndex == 0){
            workerOnePossibleActions.get(WorkerActionType.BUILD_DOME).forEach(this::lightUpSpaces);
        }
        if(selectedWorkerIndex == 1){
            workerTwoPossibleActions.get(WorkerActionType.BUILD_DOME).forEach(this::lightUpSpaces);
        }
        selectedAction = WorkerActionType.BUILD_DOME;
        buildDomeButton.setDisable(true);
    }

    public void undo(ActionEvent event) {
        if(selectedAction == WorkerActionType.MOVE && gameView.getCurrentStatus() == GameStatus.PLAYING){
            undoButton.setDisable(false);
        }
    }

    public void endTurn(ActionEvent event) {
        client.gameViewInputExec(gameView, "end");

    }

    public void selectSpace(MouseEvent mouseEvent) {
        if(isYourTurn && utilityCounter < 2 && gameView.getCurrentStatus() == GameStatus.PLACING) {
            placingBoardInteraction(mouseEvent);
        }
        if(isYourTurn && gameView.getCurrentStatus() == GameStatus.PLAYING ){
            selectWorkerInteraction(mouseEvent);
        }
        if(selectedAction == WorkerActionType.MOVE && isYourTurn){
            actionSelection(mouseEvent, WorkerActionType.MOVE);
            //moveSelection(mouseEvent);
        }
        if(selectedAction == WorkerActionType.BUILD && isYourTurn){
            actionSelection(mouseEvent, WorkerActionType.BUILD);
        }
        if(selectedAction == WorkerActionType.BUILD_DOME && isYourTurn){
            actionSelection(mouseEvent, WorkerActionType.BUILD_DOME);
        }
    }

    /*private void moveSelection(MouseEvent mouseEvent) {
        Node source = (Node) mouseEvent.getSource();
        int newSelectedX;
        int newSelectedY;
        if (GridPane.getRowIndex(source) == null) {
            newSelectedX = 0;
        } else {
            newSelectedX = GridPane.getRowIndex(source);
        }
        if (GridPane.getColumnIndex(source) == null) {
            newSelectedY = 0;
        } else {
            newSelectedY = GridPane.getColumnIndex(source);
        }
        List<Vector2> vector = this.getPossibleWorkerAction(selectedWorkerIndex).get(WorkerActionType.MOVE);
        for(Vector2 v : vector){
            if(v.getX() == newSelectedX && v.getY() == newSelectedY){
                selectedX = newSelectedX;
                selectedY = newSelectedY;
                System.out.println("Worker: " + selectedWorkerIndex);
                System.out.println("Coordinates: " + newSelectedX + " " + newSelectedY);
                client.gameViewInputExec(gameView, "move");
            }
        }
        clearSpaces();
    }*/

    private void actionSelection(MouseEvent mouseEvent, WorkerActionType type) {
        Node source = (Node) mouseEvent.getSource();
        int newSelectedX;
        int newSelectedY;
        if (GridPane.getRowIndex(source) == null) {
            newSelectedX = 0;
        } else {
            newSelectedX = GridPane.getRowIndex(source);
        }
        if (GridPane.getColumnIndex(source) == null) {
            newSelectedY = 0;
        } else {
            newSelectedY = GridPane.getColumnIndex(source);
        }
        List<Vector2> vector = this.getPossibleWorkerAction(selectedWorkerIndex).get(type);
        for(Vector2 v : vector){
            if(v.getX() == newSelectedX && v.getY() == newSelectedY){
                selectedX = newSelectedX;
                selectedY = newSelectedY;
                System.out.println("Worker: " + selectedWorkerIndex);
                System.out.println("Coordinates: " + newSelectedX + " " + newSelectedY);
                String command = type.toString().toLowerCase();
                client.gameViewInputExec(gameView, command);
            }
        }
        clearSpaces();
    }

    private void clearSpaces() {
        ObservableList<Node> children = boardGrid.getChildren();

        for (Node node : children) {
            StackPane space = (StackPane) node;
            space.setStyle("-fx-background-color: transparent");
        }
    }

    public Node getNodeByRowColumnIndex (int row, int column, GridPane gridPane) {
        Node result = null;
        ObservableList<Node> children = gridPane.getChildren();

        for (Node node : children) {
            int newSelectedX;
            int newSelectedY;
            if(GridPane.getRowIndex(node) == null){
                newSelectedX = 0;
            }
            else {
                newSelectedX = GridPane.getRowIndex(node);
            }
            if(GridPane.getColumnIndex(node) == null){
                newSelectedY = 0;
            }
            else {
                newSelectedY = GridPane.getColumnIndex(node);
            }
            if(newSelectedX == row && newSelectedY == column) {
                result = node;
                break;
            }
        }

        return result;
    }

    private boolean areAllWorkerPlaced(){
        int workerCounter = 0;
        ObservableList<Node> children = boardGrid.getChildren();
        for (Node node : children){
            if(isSpaceOccupied((StackPane)node)){
                workerCounter++;
            }
        }
        System.out.println("Number of worker placed: " + workerCounter);
        return workerCounter == numberOfPlayers * 2 - 1;
    }

    private boolean isSpaceOccupied(StackPane space){
        return space.getChildren().contains(workerOne) ||
                space.getChildren().contains(workerTwo) ||
                space.getChildren().contains(opponentOneWorkerOne) ||
                space.getChildren().contains(opponentOneWorkerTwo) ||
                space.getChildren().contains(opponentTwoWorkerOne) ||
                space.getChildren().contains(opponentTwoWorkerTwo);
    }

    public void showChosenGods(ActionEvent event) {//To be removed
        System.out.println("Chosen Gods:");
        this.availableGodsChoseByTheChallenger.forEach(System.out::println);
    }

    public void showFirstPlayer(ActionEvent event) { //To be removed
        System.out.println("Chosen Gods:");
        System.out.println(firstPlayerName);
    }

    public void updateWorld(Space changedSpace){
        Service<Void> service = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        //Background work
                        final CountDownLatch latch = new CountDownLatch(1);
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    int x = changedSpace.getPosition().getX();
                                    int y = changedSpace.getPosition().getY();
                                    StackPane space = (StackPane)getNodeByRowColumnIndex(x, y, boardGrid);
                                    if(changedSpace.isOccupiedByWorker() && !isSpaceOccupied(space) && changedSpace.getWorkerData().getPlayer().equals(username)){
                                        if(changedSpace.getWorkerData().getIndex() == 0){
                                            space.getChildren().add(workerOne);
                                        }
                                        if(changedSpace.getWorkerData().getIndex() == 1){
                                            space.getChildren().add(workerTwo);
                                        }
                                    }
                                    if(changedSpace.isOccupiedByWorker() && !isSpaceOccupied(space) && !changedSpace.getWorkerData().getPlayer().equals(username)){
                                        if(changedSpace.getWorkerData().getPlayer().equals(opponentOne.getText())){
                                            if(changedSpace.getWorkerData().getIndex() == 0){
                                                space.getChildren().add(opponentOneWorkerOne);
                                            }
                                            if(changedSpace.getWorkerData().getIndex() == 1){
                                                space.getChildren().add(opponentOneWorkerTwo);
                                            }
                                        }
                                        if(changedSpace.getWorkerData().getPlayer().equals(opponentTwo.getText())){
                                            if(changedSpace.getWorkerData().getIndex() == 0){
                                                space.getChildren().add(opponentTwoWorkerOne);
                                            }
                                            if(changedSpace.getWorkerData().getIndex() == 1){
                                                space.getChildren().add(opponentTwoWorkerTwo);
                                            }
                                        }
                                    }
                                    if(!changedSpace.isOccupiedByWorker() && isSpaceOccupied(space)){
                                        space.getChildren().removeAll();
                                    }
                                    if(changedSpace.getLevel() == 0){
                                        space.setStyle("-fx-background-image: none;");
                                    }
                                    if(changedSpace.getLevel() == 1){
                                        String img = getClass().getResource("/images/ONE.png").toExternalForm();
                                        space.setStyle("-fx-background-image: url('" + img +"');" + "-fx-background-size: cover;");

                                    }
                                    if(changedSpace.getLevel() == 2){
                                        String img = getClass().getResource("/images/TWO.png").toExternalForm();
                                        space.setStyle("-fx-background-image: url('" + img +"');" + "-fx-background-size: cover;");

                                    }
                                    if(changedSpace.getLevel() == 3){
                                        String img = getClass().getResource("/images/THREE.png").toExternalForm();
                                        space.setStyle("-fx-background-image: url('" + img +"');" + "-fx-background-size: cover;");

                                    }
                                    if(changedSpace.isOccupiedByDome()){
                                        String img = getClass().getResource("/images/DOME.png").toExternalForm();
                                        space.setStyle("-fx-background-image: url('" + img +"');" + "-fx-background-size: cover;");

                                    }
                                    if(gameView.getCurrentStatus() == GameStatus.PLAYING){
                                        sameWorkerSelect();
                                    }

                                } catch (IndexOutOfBoundsException e) {
                                    e.printStackTrace();
                                } finally{
                                    latch.countDown();
                                }
                            }
                        });
                        latch.await();
                        //Keep with the background work
                        return null;
                    }
                };
            }
        };
        service.start();


    }

    public void sameWorkerSelect(){
        Service<Void> service = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        //Background work
                        final CountDownLatch latch = new CountDownLatch(1);
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    if(selectedWorkerIndex == 0) {
                                        client.gameViewInputExec(gameView, "validate0");
                                    }
                                    if(selectedWorkerIndex == 1) {
                                        client.gameViewInputExec(gameView, "validate1");
                                    }
                                    activateButtons();
                                } catch (IndexOutOfBoundsException e) {
                                    e.printStackTrace();
                                } finally{
                                    latch.countDown();
                                }
                            }
                        });
                        latch.await();
                        //Keep with the background work
                        return null;
                    }
                };
            }
        };
        service.start();


    }

    private void placingBoardInteraction(MouseEvent mouseEvent){
        Node source = (Node) mouseEvent.getSource();
        int newSelectedX;
        int newSelectedY;
        if (GridPane.getRowIndex(source) == null) {
            newSelectedX = 0;
        } else {
            newSelectedX = GridPane.getRowIndex(source);
        }
        if (GridPane.getColumnIndex(source) == null) {
            newSelectedY = 0;
        } else {
            newSelectedY = GridPane.getColumnIndex(source);
        }
        StackPane space = (StackPane)getNodeByRowColumnIndex(newSelectedX, newSelectedY, boardGrid);
        if(!isSpaceOccupied(space)) { //the space is not occupied
            System.out.println("Space selected: " + newSelectedX + " " + newSelectedY);
            if (selectedX == -1 && selectedY == -1) {
                selectedX = newSelectedX;
                selectedY = newSelectedY;
                StackPane newSpace = (StackPane) getNodeByRowColumnIndex(selectedX, selectedY, boardGrid);
                newSpace.setStyle("-fx-background-color: aqua");
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to place the worker in that space?", ButtonType.YES, ButtonType.NO);
                alert.setTitle("Message");
                alert.setHeaderText("You received a message!");
                Optional<ButtonType> result = alert.showAndWait();
                StackPane oldSpace = (StackPane) getNodeByRowColumnIndex(selectedX, selectedY, boardGrid);
                oldSpace.setStyle("-fx-background-color: transparent");
                if(result.get() == ButtonType.YES) {
                    utilityCounter++;
                    placeWorker();
                }

            }
        }
    }

    private void selectWorkerInteraction(MouseEvent mouseEvent){
        Node source = (Node) mouseEvent.getSource();
        int newSelectedX;
        int newSelectedY;
        if (GridPane.getRowIndex(source) == null) {
            newSelectedX = 0;
        } else {
            newSelectedX = GridPane.getRowIndex(source);
        }
        if (GridPane.getColumnIndex(source) == null) {
            newSelectedY = 0;
        } else {
            newSelectedY = GridPane.getColumnIndex(source);
        }
        StackPane space = (StackPane)getNodeByRowColumnIndex(newSelectedX, newSelectedY, boardGrid);
        if(isSpaceOccupied(space)) { //the space is occupied
            if(space.getChildren().contains(workerOne)){
                if(this.selectedWorkerIndex == 0){
                    this.selectedWorkerIndex = -1;
                    space.setStyle("-fx-background-color: transparent");
                    deactivateButtons();
                }
                else if(this.selectedWorkerIndex == 1){
                    StackPane oldSpace = (StackPane) whereIsWorker(workerTwo);
                    oldSpace.setStyle("-fx-background-color: transparent");
                    this.selectedWorkerIndex = 0;
                    space.setStyle("-fx-background-color: rgba(150, 192, 235, 0.7)");
                    client.gameViewInputExec(gameView, "select");
                    activateButtons();
                }
                else if(this.selectedWorkerIndex == -1){
                    this.selectedWorkerIndex = 0;
                    space.setStyle("-fx-background-color: rgba(150, 192, 235, 0.7)");
                    client.gameViewInputExec(gameView, "select");
                    activateButtons();
                }
            }
            else if(space.getChildren().contains(workerTwo)){
                if(this.selectedWorkerIndex == 0){
                    StackPane oldSpace = (StackPane) whereIsWorker(workerOne);
                    oldSpace.setStyle("-fx-background-color: transparent");
                    this.selectedWorkerIndex = 1;
                    space.setStyle("-fx-background-color: rgba(150, 192, 235, 0.7)");
                    client.gameViewInputExec(gameView, "select");
                    activateButtons();
                }
                else if(this.selectedWorkerIndex == 1){
                    this.selectedWorkerIndex = -1;
                    space.setStyle("-fx-background-color: transparent");
                    deactivateButtons();
                }
                else if(this.selectedWorkerIndex == -1){
                    this.selectedWorkerIndex = 1;
                    space.setStyle("-fx-background-color: rgba(150, 192, 235, 0.7)");
                    client.gameViewInputExec(gameView, "select");
                    activateButtons();
                }
            }
            else{
                this.selectedWorkerIndex = -1;
            }

            activateButtons();
        }
    }

    private void activateButtons(){
        if(this.selectedWorkerIndex == 0){
            System.out.println("Worker 0: ");
            workerOnePossibleActions.keySet().forEach(System.out::println);
            if(workerOnePossibleActions.containsKey(WorkerActionType.MOVE)){
                moveButton.setDisable(false);
            }
            if(workerOnePossibleActions.containsKey(WorkerActionType.BUILD)){
                buildButton.setDisable(false);
            }

            if(workerOnePossibleActions.containsKey(WorkerActionType.BUILD_DOME)){
                buildDomeButton.setDisable(false);
            }
        }
        if(this.selectedWorkerIndex == 1){
            System.out.println("Worker 1: ");
            workerTwoPossibleActions.keySet().forEach(System.out::println);
            if(workerTwoPossibleActions.containsKey(WorkerActionType.MOVE)){
                moveButton.setDisable(false);
            }
            if(workerTwoPossibleActions.containsKey(WorkerActionType.BUILD)){
                buildButton.setDisable(false);
            }

            if(workerTwoPossibleActions.containsKey(WorkerActionType.BUILD_DOME)){
                buildDomeButton.setDisable(false);
            }
        }
    }

    private void deactivateButtons(){
        moveButton.setDisable(true);
        buildButton.setDisable(true);
        buildDomeButton.setDisable(true);
    }

    private void lightUpSpaces(Vector2 vector){
        ObservableList<Node> children = boardGrid.getChildren();
        for (Node node : children) {
            StackPane space = (StackPane) node;
            int newSelectedX;
            int newSelectedY;
            if(GridPane.getRowIndex(node) == null){
                newSelectedX = 0;
            }
            else {
                newSelectedX = GridPane.getRowIndex(node);
            }
            if(GridPane.getColumnIndex(node) == null){
                newSelectedY = 0;
            }
            else {
                newSelectedY = GridPane.getColumnIndex(node);
            }
            if(newSelectedX == vector.getX() && newSelectedY == vector.getY()){
                space.setStyle("-fx-background-color: rgba(0, 0, 252, 0.7)");
            }
        }

    }

    private Node whereIsWorker(ImageView worker){

        Node result = null;
        ObservableList<Node> children = boardGrid.getChildren();

        for (Node node : children) {
            StackPane space = (StackPane) node;
            if(space.getChildren().contains(worker)) {
                result = node;
                break;
            }
        }

        return result;
    }


    private Map<WorkerActionType, List<Vector2>> getPossibleWorkerAction(int i){
        if(i == 0){
            return workerOnePossibleActions;
        }
        else{
           return workerTwoPossibleActions;
        }
    }

    private enum workerColor{
        RED,
        BLUE,
        GREEN;
    }

}
