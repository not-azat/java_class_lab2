package controller;

import app.Master;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import model.Location;
import org.jetbrains.annotations.*;

import java.io.IOException;

public class LocationItemController {

    @FXML private Label nameLabel;
    @FXML private Label locationLabel;
    @FXML private BorderPane root;
    @Nullable private Location location;

    LocationItemController() {
        System.out.println("LocationItemController()");
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/locationItem.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Pane getRoot() {
        return root;
    }

    public void setLocation(@NotNull Location location) {
        this.location = location;
        locationLabel.setText(location.getName());
        nameLabel.setText(location.getLatitude() + ", " + location.getLongitude());
    }

}
