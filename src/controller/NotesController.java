package controller;

import app.Detail;
import app.Master;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import model.Location;
import model.Note;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static controller.LocationsController.*;


public class NotesController {
    @FXML private Parent view;
    @FXML private Button addButton;
    @FXML private Button changeButton;
    @FXML private Button removeButton;
    @FXML private ListView<Note> notesListView;
    private ObservableList<Note> data = FXCollections.observableArrayList(
            new Note(1, "note 1", 1),
            new Note(2, "note 2", 1),
            new Note(3, "note 3", 1),
            new Note(4, "note 4", 2),
            new Note(5, "note 5", 2),
            new Note(6, "note 6", 1));

    private EventBus detailEventBus;
    private final EventBus masterEventBus;
    @Nullable private Location location;

    @Inject
    public NotesController(@Master EventBus masterEventBus, @Detail EventBus detailEventBus) {
        System.out.println("NotesController.[init] masterEventBus = " + masterEventBus + ", detailEventBus = " + detailEventBus);
        this.masterEventBus = masterEventBus;
        this.detailEventBus = detailEventBus;
    }

    @Subscribe
    public void handleLocationSelectedEvent(LocationSelectedEvent event) {
        System.out.println("NotesController: LocationSelectedEvent locationId = " + event.location.getId());
    }

    private void setLocation(@NotNull Location location) {
        if (location != this.location) {
            // reinitialize here
        }
    }

    @FXML
    void initialize() {
        System.out.println("NotesController.initialize()");
        masterEventBus.register(this);
    }

    public Parent getView() {
        return view;
    }

    @FXML
    public void handleAddButtonAction(ActionEvent event) {
        System.out.println("NotesController.handleAddButtonAction()");
        detailEventBus.post(new AddButtonClickedEvent());
    }

    @FXML
    public void handleChangeButtonAction(ActionEvent event) {
        System.out.println("NotesController.handleChangeButtonAction()");
        detailEventBus.post(new ChangeButtonClickedEvent());
    }

    @FXML
    public void handleRemoveButtonAction(ActionEvent event) {
        System.out.println("NotesController.handleRemoveButtonAction()");
        detailEventBus.post(new RemoveButtonClickedEvent());
    }

    private static class AddButtonClickedEvent {}

    private static class ChangeButtonClickedEvent {}

    private static class RemoveButtonClickedEvent {}
}
