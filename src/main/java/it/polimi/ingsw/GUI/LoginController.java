package it.polimi.ingsw.GUI;


import javafx.event.ActionEvent;

import javafx.fxml.Initializable;
import javafx.scene.Node;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class LoginController implements Initializable {


    public AnchorPane background;

    public TextField usernameTextField;

    public Button enterNameButton;

    public Label welcomeMessage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        
    }

    public void goBack() throws IOException {
        GUIApp.setRoot("/views/serverChoiceScreen");
    }

    public void enterName(ActionEvent event) {
        try {
            Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();
            GUIApp.getClient().run(usernameTextField.getText(), window);
        }catch (Exception e){
            this.welcomeMessage.setText("This username is already taken!");
            welcomeMessage.setStyle("-fx-text-fill: red");
        }

    }

}
