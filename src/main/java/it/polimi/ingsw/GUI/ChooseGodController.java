package it.polimi.ingsw.GUI;

import it.polimi.ingsw.models.game.gods.GodType;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;


import java.net.URL;
import java.util.*;

public class ChooseGodController implements Initializable {

    List<GodType> listOfGods = GodType.getListOfGods();

    Map<ImageView, GodType> chosenGods = new HashMap<>();

    int currentShowingGod = 0;

    public ImageView godImage;

    public ImageView chosenGodOne;

    public ImageView chosenGodTwo;

    public ImageView chosenGodThree;

    public Button confirmButton;

    public Button discardButton;

    public void goLeft(ActionEvent event) {
        if(currentShowingGod == 0){
            currentShowingGod = 8;
        }
        else{
            currentShowingGod--;
        }
        System.out.println("Current god: " + currentShowingGod);
        loadImage();
    }

    public void goRight(ActionEvent event) {
        if(currentShowingGod == 8){
            currentShowingGod = 0;
        }
        else{
            currentShowingGod++;
        }
        System.out.println("Current god: " + currentShowingGod);
        loadImage();
    }

    private void loadImage(){
        Image image = new Image((getClass().getResource("/images/" + getCurrentShowingGod().toString() + ".png")).toString());
        godImage.setImage(image);
    }

    private GodType getCurrentShowingGod(){
        return listOfGods.get(currentShowingGod);
    }

    private boolean isGodAlreadyChosen(GodType god){
        for(ImageView k : chosenGods.keySet()){
            if(chosenGods.get(k) != null) {
                if(chosenGods.get(k).equals(god)){
                    return true;
                }
            }
        }
        return false;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        chosenGods.put(chosenGodOne, null);
        chosenGods.put(chosenGodTwo, null);
        chosenGods.put(chosenGodThree, null);
        discardButton.setDisable(true);
        confirmButton.setDisable(true);
        loadImage();
    }

    public void godClicked(MouseEvent mouseEvent) {
        Image image = new Image((getClass().getResource("/images/" + getCurrentShowingGod().toString() + ".png")).toString());
        if(!isGodAlreadyChosen(getCurrentShowingGod())) {
            if (chosenGods.get(chosenGodOne) == null) {
                chosenGodOne.setImage(image);
                chosenGods.replace(chosenGodOne, getCurrentShowingGod());
            } else if (chosenGods.get(chosenGodTwo) == null) {
                chosenGodTwo.setImage(image);
                chosenGods.replace(chosenGodTwo, getCurrentShowingGod());
            } else if (chosenGods.get(chosenGodThree) == null) {
                chosenGodThree.setImage(image);
                chosenGods.replace(chosenGodThree, getCurrentShowingGod());
            }
        }
    }

    public void godDiscarded(MouseEvent mouseEvent) {
        ImageView source = (ImageView) mouseEvent.getSource();
        Image image = new Image((getClass().getResource("/images/blankGodCard.png")).toString());
        if(chosenGods.get(source) != null){
            source.setImage(image);
            chosenGods.replace(source, null);
        }
    }

    public void confirmChoice(ActionEvent event) {
    }

    public void discardChoice(ActionEvent event) {
    }
}
