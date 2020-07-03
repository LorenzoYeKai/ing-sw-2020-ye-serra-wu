package it.polimi.ingsw.GUI;

import it.polimi.ingsw.InternalError;
import it.polimi.ingsw.NotExecutedException;
import it.polimi.ingsw.controller.game.GameController;
import it.polimi.ingsw.models.game.GameStatus;
import it.polimi.ingsw.models.game.Space;
import it.polimi.ingsw.models.game.gods.God;
import it.polimi.ingsw.models.game.gods.GodType;
import it.polimi.ingsw.requests.RequestProcessor;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
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

    private ImageView workerOne;

    private ImageView workerTwo;

    private ImageView opponentOneWorkerOne;

    private ImageView opponentOneWorkerTwo;

    private ImageView opponentTwoWorkerOne;

    private ImageView opponentTwoWorkerTwo;

    private workerColor color;

    private workerColor opponentOneColor;

    private workerColor opponentTwoColor;

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
        this.messageLabel.setText("A game has started!");
        this.isYourTurn = false;
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
        endTurnButton.setDisable(true);

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
        System.out.println("Current status: " + currentStatus.toString());
        switch (currentStatus) {
            case CHOOSING_GODS -> chooseGod(this);
            case PLACING -> yourTurnMessage(GameStatus.PLACING, "Place your workers and then click End Turn");
            case PLAYING -> yourTurnMessage(GameStatus.PLAYING, "Select a worker and make a move");
            //case ENDED -> ;*/
            default -> throw new InternalError("Not implemented yet");
        }
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
        client.gameViewInputExec(gameView, "place");
        if(utilityCounter == 2){
            if(areAllWorkerPlaced()){
                client.gameViewInputExec(gameView, "play");
            }
            endTurnButton.setDisable(false);
        }
        System.out.println("Are all workers placed? -> " + areAllWorkerPlaced());
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
    }

    public void build(ActionEvent event) {
    }

    public void buildDome(ActionEvent event) {
    }

    public void undo(ActionEvent event) {
    }

    public void endTurn(ActionEvent event) {
        client.gameViewInputExec(gameView, "end");
    }

    public void selectSpace(MouseEvent mouseEvent) {
        if(isYourTurn && utilityCounter < 2) {
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
                    selectedX = -1;
                    selectedY = -1;
                }
            }
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
        return workerCounter == numberOfPlayers * 2;
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
                                        space.setStyle("-fx-background-image: none");
                                    }
                                    if(changedSpace.getLevel() == 1){
                                        String img = getClass().getResource("/images/ONE.png").toExternalForm();
                                        space.setStyle("-fx-background-image: url('" + img +"')");
                                    }
                                    if(changedSpace.getLevel() == 2){
                                        String img = getClass().getResource("/images/TWO.png").toExternalForm();
                                        space.setStyle("-fx-background-image: url('" + img +"')");
                                    }
                                    if(changedSpace.getLevel() == 3){
                                        String img = getClass().getResource("/images/THREE.png").toExternalForm();
                                        space.setStyle("-fx-background-image: url('" + img +"')");
                                    }
                                    if(changedSpace.isOccupiedByDome()){
                                        String img = getClass().getResource("/images/DOME.png").toExternalForm();
                                        space.setStyle("-fx-background-image: url('" + img +"')");
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

    private enum workerColor{
        RED,
        BLUE,
        GREEN;
    }

}
