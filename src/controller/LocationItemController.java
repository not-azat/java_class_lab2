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
    private final EventBus masterEventBus;
    @Nullable private Location location;

    LocationItemController(EventBus masterEventBus) {
        System.out.println("LocationItemController.[init] masterEventBus = " + masterEventBus);
        this.masterEventBus = masterEventBus;
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

    public Pane getRoot() {
        return root;
    }

    public void setLocation(@NotNull Location location) {
        this.location = location;
        locationLabel.setText(location.getName());
        nameLabel.setText(location.getLatitude() + ", " + location.getLongitude());
    }

}
