module it.polimi.ingsw {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens it.polimi.ingsw to javafx.fxml;
    exports it.polimi.ingsw.GUI;
}