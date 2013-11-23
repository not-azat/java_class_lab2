package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import org.jetbrains.annotations.*;

import java.io.IOException;

/**
 * User: azat, Date: 23.11.13, Time: 22:55
 */
public class LocationItemController {

    @FXML private Parent view;
    @FXML private Label nameLabel;
    @FXML private Label locationLabel;

    LocationItemController() {
        try {
            System.out.println("LocationItemController.[init]");
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/locationItem.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void initialize() {
        System.out.println("LocationItemController.initialize()");
    }

    public void setInfo(@NotNull String name, @NotNull Double latitude, @NotNull Double longitude) {
        nameLabel.textProperty().setValue(name);
        locationLabel.textProperty().setValue(latitude.toString() + " " + longitude.toString());
    }

    public Parent getView() {
        return view;
    }

}
