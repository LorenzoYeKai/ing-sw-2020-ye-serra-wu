package it.polimi.ingsw.GUI;

import it.polimi.ingsw.NotExecutedException;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class ChooseFirstPlayerController {

    public Button confirmPlayerButton;

    public Label firstPlayerLabel;

    public Label secondPlayerLabel;

    public Label thirdPlayerLabel;

    public AnchorPane backgroundArea;

    private Label selectedPlayer;

    private GameGUIController primaryScene;

    public void initData(GameGUIController primaryScene){
        this.primaryScene = primaryScene;
        if(primaryScene.getListOfPlayers().size() == 3){
            firstPlayerLabel.setText(primaryScene.getListOfPlayers().get(0));
            secondPlayerLabel.setText(primaryScene.getListOfPlayers().get(1));
            thirdPlayerLabel.setText(primaryScene.getListOfPlayers().get(2));
        }
        else{
            backgroundArea.getChildren().remove(secondPlayerLabel);
            firstPlayerLabel.setText(primaryScene.getListOfPlayers().get(0));
            thirdPlayerLabel.setText(primaryScene.getListOfPlayers().get(1));
        }
    }

    public void playerSelected(MouseEvent mouseEvent) {
        Label source = (Label) mouseEvent.getSource();
        if (selectedPlayer != null) {//if a player is already selected
            if (selectedPlayer.equals(source)) { //if it's the same player, deselect's it
                source.setStyle("-fx-background-color: transparent;");
                selectedPlayer = null;
                confirmPlayerButton.setDisable(true);
            } else { //if is another player
                source.setStyle("-fx-background-color: rgba(99, 99, 102, 0.5);");
                selectedPlayer.setStyle("-fx-background-color: transparent;");
                selectedPlayer = source;
                confirmPlayerButton.setDisable(false);
            }
        } else {//if there is not a selected player yet
            source.setStyle("-fx-background-color: rgba(99, 99, 102, 0.5);");
            selectedPlayer = source;
            confirmPlayerButton.setDisable(false);
        }
    }

    public void confirmPlayer(ActionEvent event) throws NotExecutedException, IOException {
        this.primaryScene.setFirstPlayerName(selectedPlayer.getText());
        this.primaryScene.sendChallengerChoices();
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }


}
