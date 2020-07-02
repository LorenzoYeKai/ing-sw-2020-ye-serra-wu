package it.polimi.ingsw.GUI;

import it.polimi.ingsw.views.lobby.GUILobbyView;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CountDownLatch;

public class LobbyGUIController {

    private GUILobbyView view;

    private String username;

    private GUIClient client;

    private Set<String> onlinePlayers;

    private Set<String> availableRooms;

    private RoomController roomController;

    public VBox playerListBox;

    public VBox roomListBox;

    public Label usernameLabel;

    public Button joinButton;

    private List<Label> onlinePlayersLabels;

    private List<Label> availableRoomsLabels;

    private Label selectedRoom;


    public void initData(String username, GUIClient client) {
        this.username = username;
        usernameLabel.setText("Your username: " + this.username);
        this.onlinePlayersLabels = new ArrayList<>();
        this.onlinePlayers = new TreeSet<>();
        this.availableRoomsLabels = new ArrayList<>();
        this.availableRooms = new TreeSet<>();
        this.client = client;
        this.selectedRoom = null;
        this.roomController = null;
        this.joinButton.setDisable(true);
        //updateOnlinePlayers();

    }

    public void createRoom(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/room.fxml"));
        Parent lobby = loader.load();

        //access LobbyGUIController and passing the username

        this.roomController = loader.getController();
        this.roomController.initData(username, client, view, username);

        Stage window = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();

        Scene lobbyScene = new Scene(lobby);

        window.setScene(lobbyScene);
        window.show();

        this.client.viewInputExec(this.view, "host", "");
    }

    public void joinRoom(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/room.fxml"));
        Parent lobby = loader.load();

        //access LobbyGUIController and passing the username

        this.roomController = loader.getController();
        this.roomController.initData(username, client, view, selectedRoom.getText());

        Stage window = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();

        Scene lobbyScene = new Scene(lobby);

        window.setScene(lobbyScene);
        window.show();

        this.client.viewInputExec(this.view, "join", selectedRoom.getText());
        System.out.println("joinRoom");
    }

    public String getUsername(){
        return this.username;
    }

    public int getNumberOfOnlinePlayers (){
        return onlinePlayersLabels.size();
    }

    public int getNumberOfAvailableRooms (){
        return availableRoomsLabels.size();
    }

    public void updateOnlinePlayers()  {
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
                                    if(!onlinePlayersLabels.isEmpty()) {
                                        playerListBox.getChildren().clear();
                                        onlinePlayersLabels.clear();
                                    }
                                    onlinePlayers.forEach(this::addPlayer);
                                }finally{
                                    latch.countDown();
                                }
                            }

                            private void addPlayer(String username){
                                Label label = new Label(username); //the text on the button will be the player's name that will be acquired from the server in the real method
                                label.setPrefWidth(540);
                                label.setId(label.getText());
                                label.setFont(new Font("Arial Black", 18));
                                label.setStyle("-fx-text-fill: #2F80ED");
                                label.setAlignment(Pos.CENTER);
                                onlinePlayersLabels.add(label);
                                playerListBox.getChildren().add(label);
                                System.out.println("Number of players online: " + getNumberOfOnlinePlayers());
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

    public void updateAvailableRooms(){
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
                                    if(!availableRoomsLabels.isEmpty()) {
                                        roomListBox.getChildren().clear();
                                        availableRoomsLabels.clear();
                                    }
                                    availableRooms.forEach(this::addRoom);
                                }finally{
                                    latch.countDown();
                                }
                            }

                            private void addRoom(String roomName){
                                Label label = new Label(roomName); //the text on the button will be the player's name that will be acquired from the server in the real method
                                label.setPrefWidth(540);
                                label.setId(roomName + "Room");
                                label.setFont(new Font("Arial Black", 18));
                                label.setStyle("-fx-text-fill: #00FF66");
                                label.setAlignment(Pos.CENTER);
                                label.setOnMouseClicked(this::roomSelected);
                                availableRoomsLabels.add(label);
                                roomListBox.getChildren().add(label);
                                System.out.println("Number rooms available: " + getNumberOfAvailableRooms());
                            }

                            private void roomSelected(MouseEvent mouseEvent){
                                Label source = (Label) mouseEvent.getSource();
                                if(selectedRoom != null){
                                    if(selectedRoom.equals(source)){
                                        source.setStyle("-fx-background-color: transparent;");
                                        selectedRoom = null;
                                        joinButton.setDisable(true);
                                    }
                                    else{
                                        source.setStyle("-fx-background-color: rgba(99, 99, 102, 0.5);");
                                        selectedRoom.setStyle("-fx-background-color: transparent;");
                                        selectedRoom = source;
                                        joinButton.setDisable(false);
                                    }
                                }
                                else{
                                    source.setStyle("-fx-background-color: rgba(99, 99, 102, 0.5);");
                                    selectedRoom = source;
                                    joinButton.setDisable(false);
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

    public void setOnlinePlayers(Set<String> usernames){
        this.onlinePlayers = usernames;
    }

    public void setAvailableRooms(Set<String> availableRooms){
        this.availableRooms = availableRooms;
    }

    public void setView(GUILobbyView view){
        this.view = view;

    }

    public void updatePlayersInTheRoom() {
        if(this.roomController != null){
            this.roomController.updatePlayersInTheRoom();
        }
        System.out.println("Players in the room:");
        view.getPlayersInTheRoom().forEach(System.out::println);
    }

    public void returnFromRoom(String username, GUIClient client, GUILobbyView view){ //reload the previous state of the lobby after leaving a room
        this.initData(username, client);
        this.setView(view);
        this.view.setLobbyGUIController(this);
        this.onlinePlayers = this.view.getLobbyUsers();
        this.availableRooms = this.view.getLobbyRooms();
        updateOnlinePlayers();
        updateAvailableRooms();
        System.out.println("online players: ");
        this.onlinePlayers.forEach(System.out::println);
    }

    public void receiveMessage(String message){
        if(message.endsWith("kicked") || message.equals("[SYSTEM]: Host has left the room")){
            this.roomController.kicked(message);
            System.out.println("kick message received");
        }
    }
}

