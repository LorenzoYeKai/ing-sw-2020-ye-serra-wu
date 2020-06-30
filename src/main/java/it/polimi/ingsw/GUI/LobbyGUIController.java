package it.polimi.ingsw.GUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LobbyGUIController {

    private String username;

    public VBox playerListBox;

    public VBox roomListBox;

    public Label usernameLabel;

    private List<Button> onlinePlayersButtons;

    private List<Button> availableRoomsButtons;

    public void initData(String username){
        this.username = username;
        usernameLabel.setText("Your username: " + this.username);
        this.onlinePlayersButtons = new ArrayList<>();
        this.availableRoomsButtons = new ArrayList<>();
    }

    public void createRoom(ActionEvent actionEvent) {
        Button newButton = new Button("newPlayer"); //the text on the button will be the player's name that will be acquired from the server in the real method
        newButton.setPrefWidth(540);
        newButton.setId(newButton.getText());
        onlinePlayersButtons.add(newButton);
        playerListBox.getChildren().add(newButton);
        System.out.println("Number of players online: " + getNumberOfOnlinePlayers());
    }

    public void joinRoom(ActionEvent actionEvent){
        if(getNumberOfOnlinePlayers() > 0) {
            Button toBeRemovedButton = onlinePlayersButtons.get(0);
            playerListBox.getChildren().remove(toBeRemovedButton);
            onlinePlayersButtons.remove(toBeRemovedButton);
        }
        System.out.println("Number of players online: " + getNumberOfOnlinePlayers());
    }

    public void startGame(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/primary.fxml"));
        Parent lobby = loader.load();

        Scene lobbyScene = new Scene(lobby);

        Stage window = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        window.setScene(lobbyScene);
        window.show();
    }

    public String getUsername(){
        return this.username;
    }

    public int getNumberOfOnlinePlayers (){
        return onlinePlayersButtons.size();
    }

    public int getNumberOfAvailableRooms (){
        return availableRoomsButtons.size();
    }

}
