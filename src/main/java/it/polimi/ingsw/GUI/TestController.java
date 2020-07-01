package it.polimi.ingsw.GUI;

import javafx.animation.PauseTransition;
import javafx.beans.InvalidationListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TestController implements Initializable{


    private Stage stage;
    private int numberOfPlayers;

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


    public void setLowRes(){
        stage.setWidth(640);
        stage.setHeight(480);
        bottomArea.setPrefHeight(54);
        topArea.setPrefHeight(54);
        leftArea.setPrefWidth(175);
        rightArea.setPrefWidth(175);
    }

    public void setHd(){
        stage.setWidth(1280);
        stage.setHeight(720);
        bottomArea.setPrefHeight(80);
        topArea.setPrefHeight(80);
        leftArea.setPrefWidth(350);
        rightArea.setPrefWidth(350);
    }

    public void setFullHd(){
        stage.setWidth(1920);
        stage.setHeight(1080);

        bottomArea.setPrefHeight(120);
        topArea.setPrefHeight(120);
        leftArea.setPrefWidth(540);
        rightArea.setPrefWidth(540);
    }

    public void setFullScreen(){
        stage.setFullScreen(true);
        bottomArea.setPrefHeight(120);
        topArea.setPrefHeight(120);
        leftArea.setPrefWidth(540);
        rightArea.setPrefWidth(540);
    }

    private void chooseGod(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/chooseGod.fxml"));
        try {
            Parent chooseGodLoader = loader.load();
            Scene chooseGodScene = new Scene(chooseGodLoader);
            Stage chooseGodWindow = new Stage();
            chooseGodWindow.initModality(Modality.APPLICATION_MODAL);
            chooseGodWindow.setScene(chooseGodScene);
            chooseGodWindow.show();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public void init(Stage stage, int numberOfPlayers){
        this.stage = stage;
        this.numberOfPlayers = numberOfPlayers;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("ciao");
        bottomArea.setPrefHeight(80);
        topArea.setPrefHeight(80);
        leftArea.setPrefWidth(350);
        rightArea.setPrefWidth(350);
        PauseTransition delay = new PauseTransition(Duration.seconds(0.5));
        delay.setOnFinished(e -> chooseGod());
        delay.play();
    }
}
