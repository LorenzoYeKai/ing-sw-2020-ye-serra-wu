package it.polimi.ingsw.GUI;

import it.polimi.ingsw.InternalError;
import it.polimi.ingsw.NotExecutedException;
import it.polimi.ingsw.controller.game.GameController;
import it.polimi.ingsw.models.game.GameStatus;
import it.polimi.ingsw.models.game.gods.God;
import it.polimi.ingsw.models.game.gods.GodType;
import it.polimi.ingsw.requests.RequestProcessor;
import it.polimi.ingsw.views.game.GUIGameView;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

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

    public AnchorPane background;

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
        this.gameView = new GUIGameView(username, listOfPlayers, controller, this);
        client.gameViewInputExec(gameView, "join");
        System.out.println("Number of players: " + numberOfPlayers);
        if(isChallenger) {
            client.gameViewInputExec(gameView, "setup");
            PauseTransition delay = new PauseTransition(Duration.seconds(0.5));
            delay.setOnFinished(e -> chooseAvailableGod());
            delay.play();
        }
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

    public void showChosenGods(ActionEvent event) {
        System.out.println("Chosen Gods:");
        this.availableGodsChoseByTheChallenger.forEach(System.out::println);
    }

    public void showFirstPlayer(ActionEvent event) {
        System.out.println("Chosen Gods:");
        System.out.println(firstPlayerName);
    }

    public List<String> getListOfPlayers(){
        return this.listOfPlayers;
    }

    public void sendChallengerChoices() {
        System.out.println("SelectedGods: ");
        this.availableGodsChoseByTheChallenger.forEach(System.out::println);
        System.out.println(this.firstPlayerName + " -> " + listOfPlayers.indexOf(firstPlayerName));
        client.gameViewInputExec(gameView, "challengerChoice");
    }

    public void initCurrentTurn(GameStatus currentStatus){
        System.out.println("Current status: " + currentStatus.toString());
        switch (currentStatus) {
            case CHOOSING_GODS -> chooseGod();
            /*case PLACING -> ;
            case PLAYING -> ;
            case ENDED -> ;*/
            default -> throw new InternalError("Not implemented yet");
        }
    }

    private void chooseGod(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/chooseGod.fxml"));
        try {
            Parent chooseGodLoader = loader.load();
            ChooseGodController chooseGodController = loader.getController();
            chooseGodController.initData(numberOfPlayers, this, availableGods);
            Scene chooseGodScene = new Scene(chooseGodLoader);
            Stage chooseGodWindow = new Stage();
            chooseGodWindow.initModality(Modality.APPLICATION_MODAL);
            chooseGodWindow.setScene(chooseGodScene);
            chooseGodWindow.show();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public void addAvailableGods(Collection<GodType> gods){
        if(gods.size() == numberOfPlayers) {
            this.availableGods.addAll(gods);
        }
    }

    public List<GodType> getAvailableGodsChoseByTheChallenger(){
        return  this.availableGodsChoseByTheChallenger;
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
    }

    public String getFirstPlayerName() {
        return firstPlayerName;
    }
}
