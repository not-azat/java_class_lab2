package controller;

import app.Master;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;

import java.io.IOException;
import static controller.LocationsController.*;

/**
 * User: azat, Date: 23.11.13, Time: 21:39
 */
public class MainController {

    @FXML private Parent view;
    @FXML private LocationsController locationsController;
    @FXML private NotesController notesController;

    private EventBus masterEventBus;

    @Inject
    public MainController(@Master EventBus masterEventBus) {
        System.out.println("MainController.[init]");
        this.masterEventBus = masterEventBus;
    }

    @FXML
    void initialize() {
        System.out.println("MainController.initialize()");
        System.out.println("locations: " + locationsController);
        System.out.println("notes: " + notesController);
    }

    public Parent getView() {
        return view;
    }
}
