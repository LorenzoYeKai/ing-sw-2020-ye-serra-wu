package it.polimi.ingsw.GUI;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
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

    public void selectServer(ActionEvent event) throws IOException {
        if(event.getSource().equals(aws)){
            try {
                GUIApp.setClient("18.195.117.7", 12345);
                GUIApp.setRoot("/views/login");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        else{
            try {
                GUIApp.setClient("127.0.0.1", 12345);
                GUIApp.setRoot("/views/login");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
