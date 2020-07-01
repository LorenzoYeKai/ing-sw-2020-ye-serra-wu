package it.polimi.ingsw.GUI;

import it.polimi.ingsw.views.lobby.GUILobbyView;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class RoomController {

    private String username;

    private GUILobbyView view;

    private GUIClient client;

    private boolean isHost;

    public AnchorPane players;

    public Label playerLabelOne;

    public Label playerLabelTwo;

    public Label playerLabelThree;

    public Label welcomeMessage;

    public Button leaveButton;

    public Button kickButton;

    public Button startGameButton;

    public AnchorPane roomScene;

    public Label usernameLabel;

    private Label selectedPlayer;


    public void initData(String username, GUIClient client, GUILobbyView view, String hostName) {
        this.client = client;
        this.username = username;
        this.view = view;
        this.startGameButton.setDisable(true);
        this.kickButton.setDisable(true);
        this.isHost = username.equals(hostName);
        this.usernameLabel.setText("Your username: " + username);
        if(this.isHost){
            welcomeMessage.setText("Welcome! You are the host of this room");
        }
        else{
            welcomeMessage.setText("Welcome! You are in " + hostName + "'s room");
            roomScene.getChildren().remove(kickButton);
            roomScene.getChildren().remove(startGameButton);
        }
        this.selectedPlayer = null;
    }


    public void playerSelected(MouseEvent mouseEvent) {
        if(isHost) {
            Label source = (Label) mouseEvent.getSource();
            if (selectedPlayer != null) {//if a player is already selected
                if (selectedPlayer.equals(source)) { //if it's the same player, deselect's it
                    source.setStyle("-fx-background-color: transparent;");
                    selectedPlayer = null;
                    kickButton.setDisable(true);
                } else { //if is another player
                    source.setStyle("-fx-background-color: rgba(99, 99, 102, 0.5);");
                    selectedPlayer.setStyle("-fx-background-color: transparent;");
                    selectedPlayer = source;
                    kickButton.setDisable(selectedPlayer.getText().equals("Empty") || selectedPlayer.getText().equals(username));
                }
            } else {//if there is not a selected player yet
                source.setStyle("-fx-background-color: rgba(99, 99, 102, 0.5);");
                selectedPlayer = source;
                kickButton.setDisable(selectedPlayer.getText().equals("Empty") || selectedPlayer.getText().equals(username));
            }
        }
    }

    public void kickPlayer(ActionEvent event) {
        this.client.viewInputExec(this.view, "kick", this.selectedPlayer.getText());
        selectedPlayer.setStyle("-fx-background-color: transparent;");
        selectedPlayer = null;
        kickButton.setDisable(true);
        if(view.getPlayersInTheRoom().size() < 2){
            startGameButton.setDisable(true);
        }
    }

    public void leaveRoom(ActionEvent event) throws IOException {
        this.client.viewInputExec(this.view, "leave", "");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/lobby.fxml"));
        Parent lobby = loader.load();

        //loader.setController(this.view.getLobbyGUIController());
        LobbyGUIController controller = loader.getController();
        controller.returnFromRoom(username, client, this.view);

        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();

        Scene lobbyScene = new Scene(lobby);

        window.setScene(lobbyScene);
        window.show();
    }

    public void startGame(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/primary.fxml"));
        Parent lobby = loader.load();

        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();
        TestController controller = loader.getController();
        controller.init(window, this.view.getPlayersInTheRoom().size());

        Scene lobbyScene = new Scene(lobby);

        window.setScene(lobbyScene);
        window.show();

        client.stopProcessor();
    }

    public void updatePlayersInTheRoom() {
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
                                    if(view.getPlayersInTheRoom().size() == 1){
                                        playerLabelOne.setText(view.getPlayersInTheRoom().get(0));
                                        playerLabelTwo.setText("Empty");
                                        playerLabelThree.setText("Empty");
                                        startGameButton.setDisable(true);
                                    }
                                    else if(view.getPlayersInTheRoom().size() == 2){
                                        playerLabelOne.setText(view.getPlayersInTheRoom().get(0));
                                        playerLabelTwo.setText(view.getPlayersInTheRoom().get(1));
                                        playerLabelThree.setText("Empty");
                                        startGameButton.setDisable(false);
                                    }
                                    else if(view.getPlayersInTheRoom().size() == 3){
                                        playerLabelOne.setText(view.getPlayersInTheRoom().get(0));
                                        playerLabelTwo.setText(view.getPlayersInTheRoom().get(1));
                                        playerLabelThree.setText(view.getPlayersInTheRoom().get(2));
                                        startGameButton.setDisable(false);
                                    }
                                    else{
                                        System.out.println("Something gone wrong...");
                                    }
                                } catch (IndexOutOfBoundsException e) {
                                    e.printStackTrace();
                                } finally{
                                    latch.countDown();
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

    public void kicked(String message)  {
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
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.CLOSE);
                                    alert.setTitle("Message");
                                    alert.setHeaderText("You received a message!");
                                    alert.showAndWait();
                                    FXMLLoader loader = new FXMLLoader();
                                    loader.setLocation(getClass().getResource("/views/lobby.fxml"));
                                    Parent lobby = loader.load();

                                    //loader.setController(this.view.getLobbyGUIController());
                                    LobbyGUIController controller = loader.getController();
                                    controller.returnFromRoom(username, client, view);

                                    Stage window = (Stage) roomScene.getScene().getWindow();

                                    Scene lobbyScene = new Scene(lobby);

                                    window.setScene(lobbyScene);
                                    window.show();
                                    client.viewInputExec(view, "leave", "");
                                } catch (IndexOutOfBoundsException | IOException e) {
                                    e.printStackTrace();
                                } finally{
                                    latch.countDown();
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

}
