package it.polimi.ingsw.GUI;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class GUIApp extends Application {

    private static Scene scene;
    private static GUIClient client;

    @Override
    public void start(Stage stage) throws Exception {
        scene = new Scene(loadFXML("/views/serverChoiceScreen"));
        stage.setScene(scene);
        stage.setTitle("Santorini");
        stage.setResizable(false);
        stage.setMinHeight(720);
        stage.setMinWidth(1280);
        stage.setWidth(1280);
        stage.setHeight(720);
        stage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    static void setClient(String ip, int port) throws IOException {
        GUIApp.client = new GUIClient(ip, port);
    }

    static GUIClient getClient(){
        return client;
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(GUIApp.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args){
        launch(args);
    }

}
