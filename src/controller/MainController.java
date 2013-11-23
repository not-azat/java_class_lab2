package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

/**
 * User: azat, Date: 23.11.13, Time: 21:39
 */
public class MainController {

    @FXML private Parent view;
    @FXML private LocationsController locationsController;
    @FXML private NotesController notesController;

    public MainController() {
        System.out.println("MainController.[init]");
    }

    @FXML
    void initiallize() {
        System.out.println("MainController.initialize()");
        System.out.println("locations: " + locationsController);
        System.out.println("notes: " + notesController);
    }

    public Parent getView() {
        return view;
    }
}
