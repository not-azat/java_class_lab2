package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

/**
 * User: azat, Date: 23.11.13, Time: 21:12
 */
public class NotesController {
    @FXML private Button addButton;
    @FXML private Button changeButton;
    @FXML private Button removeButton;
    @FXML private ListView<String> notesListView;

    public void initialize() {
        System.out.println("NotesController.initialize()");
    }

    @FXML
    public void handleAddButtonAction(ActionEvent event) {
        System.out.println("NotesController.handleAddButtonAction()");
    }

    @FXML
    public void handleChangeButtonAction(ActionEvent event) {
        System.out.println("NotesController.handleChangeButtonAction()");
    }

    @FXML
    public void handleRemoveButtonAction(ActionEvent event) {
        System.out.println("NotesController.handleRemoveButtonAction()");
    }
}
