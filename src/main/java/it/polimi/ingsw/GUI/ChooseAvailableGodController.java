package it.polimi.ingsw.GUI;

import it.polimi.ingsw.models.game.gods.GodType;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


import java.io.IOException;
import java.net.URL;
import java.util.*;

public class ChooseAvailableGodController implements Initializable {

    private List<GodType> listOfGods = GodType.getListOfGods();

    private Map<ImageView, GodType> chosenGods = new HashMap<>();

    private int currentShowingGod = 0;

    private int numberOfPlayers;

    private GameGUIController primaryScene;

    public ImageView godImage;

    public ImageView chosenGodOne;

    public ImageView chosenGodTwo;

    public ImageView chosenGodThree;

    public Button confirmButton;

    public Button discardButton;

    public AnchorPane bottomArea;



    public void initData(int numberOfPlayers, GameGUIController primaryScene){
        this.numberOfPlayers = numberOfPlayers;
        this.primaryScene = primaryScene;
        chosenGods.put(chosenGodOne, null);
        chosenGods.put(chosenGodThree, null);
        if(numberOfPlayers == 3) {
            chosenGods.put(chosenGodTwo, null);
        }
        else{
            bottomArea.getChildren().remove(chosenGodTwo);
        }
    }

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
            } else if (chosenGods.get(chosenGodTwo) == null && chosenGods.containsKey(chosenGodTwo)) {
                chosenGodTwo.setImage(image);
                chosenGods.replace(chosenGodTwo, getCurrentShowingGod());
            } else if (chosenGods.get(chosenGodThree) == null) {
                chosenGodThree.setImage(image);
                chosenGods.replace(chosenGodThree, getCurrentShowingGod());
            }
        }
        discardButton.setDisable(false);
        confirmButton.setDisable(chosenGodsSize() != numberOfPlayers);
    }

    public void godDiscarded(MouseEvent mouseEvent) {
        ImageView source = (ImageView) mouseEvent.getSource();
        Image image = new Image((getClass().getResource("/images/blankGodCard.png")).toString());
        if(chosenGods.get(source) != null){
            source.setImage(image);
            chosenGods.replace(source, null);
        }
        discardButton.setDisable(chosenGodsSize() == 0);
        confirmButton.setDisable(chosenGodsSize() != numberOfPlayers);
    }

    private int chosenGodsSize(){
        int counter = 0;
        List<GodType> gods = new ArrayList<>(chosenGods.values());
        for (GodType god : gods) {
            if (god != null) {
                counter++;
            }
        }
        return counter;
    }

    public void confirmChoice(ActionEvent event) throws IOException {
        this.primaryScene.setAvailableGodsChoseByTheChallenger(getChosenGods());
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/chooseFirstPlayer.fxml"));
        Parent chooseFirstPlayer = loader.load();

        ChooseFirstPlayerController firstPlayerController = loader.getController();
        firstPlayerController.initData(this.primaryScene);

        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        Scene chooseFirstPlayerScene = new Scene(chooseFirstPlayer);
        stage.setScene(chooseFirstPlayerScene);
        stage.show();

        System.out.println(chosenGodsSize());
    }

    private List<GodType> getChosenGods(){
        return new ArrayList<>(chosenGods.values());
    }

    public void discardChoice(ActionEvent event) {
        chosenGods.forEach((c, v) -> chosenGods.replace(c, null));
        Image image = new Image((getClass().getResource("/images/blankGodCard.png")).toString());
        chosenGodOne.setImage(image);
        if(numberOfPlayers == 3){
            chosenGodTwo.setImage(image);
        }
        chosenGodThree.setImage(image);
        confirmButton.setDisable(true);
        discardButton.setDisable(true);
    }
}
