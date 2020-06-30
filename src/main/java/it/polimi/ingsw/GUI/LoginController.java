package it.polimi.ingsw.GUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        
    }

    public void goBack() throws IOException {
        GUIApp.setRoot("/views/serverChoiceScreen");
    }

    public void enterName(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/lobby.fxml"));
        Parent lobby = loader.load();

        //access LobbyGUIController and passing the username

        LobbyGUIController lobbyController = loader.getController();
        lobbyController.initData(usernameTextField.getText());


        Scene lobbyScene = new Scene(lobby);

        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();
        window.setScene(lobbyScene);
        window.show();
    }

}
