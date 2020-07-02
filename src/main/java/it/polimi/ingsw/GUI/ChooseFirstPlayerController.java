package it.polimi.ingsw.GUI;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

public class ChooseFirstPlayerController {

    public Button confirmPlayerButton;

    private Label selectedPlayer;

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

    public void confirmPlayer(ActionEvent event) {
    }
}
