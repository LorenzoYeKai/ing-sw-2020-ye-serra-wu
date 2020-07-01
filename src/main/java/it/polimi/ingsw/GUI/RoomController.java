package it.polimi.ingsw.GUI;

import it.polimi.ingsw.views.lobby.GUILobbyView;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
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

    private Label selectedLabel;


    public void initData(String username, GUIClient client, GUILobbyView view, String hostName) {
        this.client = client;
        this.username = username;
        this.view = view;
        this.startGameButton.setDisable(true);
        this.kickButton.setDisable(true);
        this.isHost = username.equals(hostName);
        if(this.isHost){
            welcomeMessage.setText("Welcome! You are the host of this room");
        }
        else{
            welcomeMessage.setText("Welcome! You are in " + hostName + "'s room");
            roomScene.getChildren().remove(kickButton);
            roomScene.getChildren().remove(startGameButton);
        }
        this.selectedLabel = null;
    }


    public void playerSelected(MouseEvent mouseEvent) {
        if(isHost) {
            Label source = (Label) mouseEvent.getSource();
            if (selectedLabel != null) {//if a player is already selected
                if (selectedLabel.equals(source)) { //if it's the same player, deselect's it
                    source.setStyle("-fx-background-color: transparent;");
                    selectedLabel = null;
                    kickButton.setDisable(true);
                } else { //if is another player
                    source.setStyle("-fx-background-color: rgba(99, 99, 102, 0.5);");
                    selectedLabel.setStyle("-fx-background-color: transparent;");
                    selectedLabel = source;
                    kickButton.setDisable(selectedLabel.getText().equals("Empty"));
                }
            } else {//if there is not a selected player yet
                source.setStyle("-fx-background-color: rgba(99, 99, 102, 0.5);");
                selectedLabel = source;
                kickButton.setDisable(selectedLabel.getText().equals("Empty"));
            }
        }
    }

    public void kickPlayer(ActionEvent event) {

    }

    public void leaveRoom(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/lobby.fxml"));
        Parent lobby = loader.load();

        loader.setController(this.view.getLobbyGUIController());

        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();

        Scene lobbyScene = new Scene(lobby);

        window.setScene(lobbyScene);
        window.show();
        this.client.viewInputExec(this.view, "leave", "");
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
                                        throw new IndexOutOfBoundsException("Something gone wrong...");
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
}
