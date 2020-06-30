package it.polimi.ingsw.GUI;

import it.polimi.ingsw.InternalError;
import it.polimi.ingsw.NotExecutedException;
import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.controller.game.GameController;
import it.polimi.ingsw.controller.lobby.LobbyController;
import it.polimi.ingsw.controller.lobby.remote.ClientLobbyController;
import it.polimi.ingsw.models.lobby.UserToken;
import it.polimi.ingsw.requests.RequestProcessor;
import it.polimi.ingsw.views.lobby.LobbyView;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LobbyGUIController {

    private LobbyController controller;

    private  UserToken token;

    private String username;

    private RequestProcessor processor;

    private Thread eventThread;

    public VBox playerListBox;

    public VBox roomListBox;

    public Label usernameLabel;

    private List<Label> onlinePlayersLabels;

    private List<Label> availableRoomsButtons;


    public void initData(String username, RequestProcessor processor, Thread eventThread) {
        this.username = username;
        usernameLabel.setText("Your username: " + this.username);
        this.onlinePlayersLabels = new ArrayList<>();
        this.availableRoomsButtons = new ArrayList<>();
        this.processor = processor;
        this.eventThread = eventThread;
    }

    public void createRoom(ActionEvent actionEvent) {
        addPlayer("newPlayer");
        System.out.println("Create Room");
    }

    public void joinRoom(ActionEvent actionEvent){
        /*if(getNumberOfOnlinePlayers() > 0) {
            Button toBeRemovedButton = onlinePlayersButtons.get(0);
            playerListBox.getChildren().remove(toBeRemovedButton);
            onlinePlayersButtons.remove(toBeRemovedButton);
        }
        System.out.println("Number of players online: " + getNumberOfOnlinePlayers());*/
        System.out.println("joinRoom");
    }

    public void startGame(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/primary.fxml"));
        Parent lobby = loader.load();

        Stage window = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        TestController controller = loader.getController();
        controller.init(window);

        Scene lobbyScene = new Scene(lobby);

        window.setScene(lobbyScene);
        window.show();
        System.out.println("Got game controller (Not implemented yet)");
        this.processor.requestStop();
        try {
            this.eventThread.join();
        } catch (InterruptedException e) {
            throw new InternalError(e);
        }
    }

    public String getUsername(){
        return this.username;
    }

    public int getNumberOfOnlinePlayers (){
        return onlinePlayersLabels.size();
    }

    public int getNumberOfAvailableRooms (){
        return availableRoomsButtons.size();
    }

    public void addPlayer(String username){
        Label label = new Label(username); //the text on the button will be the player's name that will be acquired from the server in the real method
        label.setPrefWidth(540);
        label.setId(label.getText());
        onlinePlayersLabels.add(label);
        playerListBox.getChildren().add(label);
        System.out.println("Number of players online: " + getNumberOfOnlinePlayers());
    }

    /*public void updateOnlinePlayers(Collection<String> userNames){
        if(!onlinePlayersLabels.isEmpty()) {
            playerListBox.getChildren().clear();
            onlinePlayersLabels.clear();
        }
        userNames.forEach(this::addPlayer);
    }*/

    /*@Override
    public void displayAvailableRooms(Collection<String> roomNames) {
        System.out.println("displayAvailableRoom");
    }

    @Override
    public void displayUserList(Collection<String> userNames) {
        playerListBox.getChildren().clear();
        onlinePlayersLabels.clear();
        userNames.forEach(this::addPlayer);
    }

    @Override
    public void notifyMessage(String author, String message) {
        System.out.println("notifyMessage");
    }

    @Override
    public void notifyRoomChanged(String newRoomName) {
        System.out.println("notifyRoomChanged");
    }

    @Override
    public void displayRoomPlayerList(Collection<String> playerList) {
        System.out.println("displayRoomPlayerList");
    }

    @Override
    public void notifyGameStarted(GameController gameController) {
        System.out.println("notifyGameStarted");
    }*/
}
