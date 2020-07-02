package it.polimi.ingsw.GUI;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ServerChoiceController implements Initializable {


    public VBox serverList;

    public Button localHost;

    public Button aws;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void selectServer(ActionEvent event) {
        try {
            if(event.getSource().equals(aws)){
                GUIApp.setClient("18.195.117.7", 12345);
            }
            else{
                GUIApp.setClient("127.0.0.1", 12345);
            }
            GUIApp.setRoot("/views/login");
        } catch (IOException e) {
            System.err.println("The server is off-line!");
            Alert alert = new Alert(Alert.AlertType.ERROR, "The server is off-line!", ButtonType.CLOSE);
            alert.setTitle("Message");
            alert.setHeaderText("You received a message!");
            alert.showAndWait();
            e.printStackTrace();
        }
    }
}
