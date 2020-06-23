package it.polimi.ingsw.GUI;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class TestController implements Initializable{


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
        Stage stage = (Stage) lowResButton.getScene().getWindow();
        stage.setWidth(640);
        stage.setHeight(480);
        bottomArea.setPrefHeight(54);
        topArea.setPrefHeight(54);
        leftArea.setPrefWidth(175);
        rightArea.setPrefWidth(175);
    }

    public void setHd(){
        Stage stage = (Stage) hdButton.getScene().getWindow();
        stage.setWidth(1280);
        stage.setHeight(720);
        bottomArea.setPrefHeight(80);
        topArea.setPrefHeight(80);
        leftArea.setPrefWidth(350);
        rightArea.setPrefWidth(350);
    }

    public void setFullHd(){
        Stage stage = (Stage) fullHdButton.getScene().getWindow();
        stage.setWidth(1920);
        stage.setHeight(1080);
        bottomArea.setPrefHeight(120);
        topArea.setPrefHeight(120);
        leftArea.setPrefWidth(540);
        rightArea.setPrefWidth(540);
    }

    public void setFullScreen(){
        Stage stage = (Stage) fullScreenButton.getScene().getWindow();
        stage.setFullScreen(true);
        bottomArea.setPrefHeight(120);
        topArea.setPrefHeight(120);
        leftArea.setPrefWidth(540);
        rightArea.setPrefWidth(540);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("ciao");
        bottomArea.setPrefHeight(54);
        topArea.setPrefHeight(54);
        leftArea.setPrefWidth(175);
        rightArea.setPrefWidth(175);
    }
}
