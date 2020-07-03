package it.polimi.ingsw.GUI;

import it.polimi.ingsw.models.game.gods.GodType;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.util.List;

public class ChooseGodController {

    private int numberOfPlayers;

    private GameGUIController controller;

    private List<GodType> availableGods;

    private ImageView selectedGod;

    public ImageView firstGod;

    public ImageView secondGod;

    public ImageView thirdGod;

    public Button confirmButton;

    public AnchorPane background;

    public void initData(int numberOfPlayers, GameGUIController gameGUIController, List<GodType> availableGods) {
        this.numberOfPlayers = numberOfPlayers;
        this.controller = gameGUIController;
        this.selectedGod = null;
        this.availableGods = availableGods;
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(0.7);
        if(availableGods.size() == 1){
            background.getChildren().remove(firstGod);
            background.getChildren().remove(thirdGod);
            secondGod.setImage(new Image((getClass().getResource("/images/" + availableGods.get(0).toString() + ".png")).toString()));
        }
        if(availableGods.size() == 2){
            background.getChildren().remove(secondGod);
            firstGod.setImage(new Image((getClass().getResource("/images/" + availableGods.get(0).toString() + ".png")).toString()));
            thirdGod.setImage(new Image((getClass().getResource("/images/" + availableGods.get(1).toString() + ".png")).toString()));
            firstGod.setEffect(colorAdjust);
            thirdGod.setEffect(colorAdjust);
        }
        else if(availableGods.size() == 3){
            firstGod.setImage(new Image((getClass().getResource("/images/" + availableGods.get(0).toString() + ".png")).toString()));
            firstGod.setImage(new Image((getClass().getResource("/images/" + availableGods.get(1).toString() + ".png")).toString()));
            thirdGod.setImage(new Image((getClass().getResource("/images/" + availableGods.get(2).toString() + ".png")).toString()));
            firstGod.setEffect(colorAdjust);
            secondGod.setEffect(colorAdjust);
            thirdGod.setEffect(colorAdjust);
        }
        this.confirmButton.setDisable(true);
    }

    public void godSelected(MouseEvent mouseEvent) {
        ColorAdjust bright = new ColorAdjust();
        bright.setBrightness(0.3);
        ColorAdjust dark = new ColorAdjust();
        dark.setBrightness(0.7);
        ImageView source = (ImageView) mouseEvent.getSource();
        if (selectedGod != null) {//if a player is already selected
            if (selectedGod.equals(source)) { //if it's the same player, deselect's it
                source.setEffect(dark);
                selectedGod = null;
                confirmButton.setDisable(true);
            } else { //if is another player
                source.setEffect(bright);
                selectedGod.setEffect(dark);
                selectedGod = source;
                confirmButton.setDisable(false);
            }
        } else {//if there is not a selected player yet
            source.setEffect(bright);
            selectedGod = source;
            confirmButton.setDisable(false);
        }
    }

    private GodType getSelectedGod(){
        if(availableGods.size() == 1){
            return availableGods.get(0);
        }
        if(availableGods.size() == 2){
            if(selectedGod.equals(firstGod)){
                return availableGods.get(0);
            }
            else{
                return availableGods.get(1);
            }
        }
        if(availableGods.size() == 3){
            if(selectedGod.equals(firstGod)){
                return availableGods.get(0);
            }
            else if(selectedGod.equals(secondGod)){
                return availableGods.get(1);
            }
            else {
                return availableGods.get(2);
            }
        }
        return null;
    }


    public void confirmGod(ActionEvent event) {
        controller.setChosenGod(getSelectedGod());
        controller.sendChosenGod(availableGods.size() == 1);
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }
}
