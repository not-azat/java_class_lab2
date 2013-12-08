package controller;

import app.Detail;
import app.Master;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import data.LocationsRepository;
import data.NotesRepository;
import data.Repository;
import data.RepositoryException;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.util.Callback;
import model.Location;
import model.Note;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

import static controller.LocationsController.*;


public class NotesController {
    @FXML private TitledPane view;
    @FXML private Button addButton;
    @FXML private Button changeButton;
    @FXML private Button removeButton;
    @FXML private ListView<Note> notesListView;
    private ObservableList<Note> data = FXCollections.observableArrayList();

    private EventBus detailEventBus;
    private final EventBus masterEventBus;
    /** Currently selected location */
    @Nullable private Location location;
    /** Currenly selected note */
    @Nullable private Note note;
    private final NotesRepository notesRepository;

    @Inject
    public NotesController(NotesRepository notesRepository, @Master EventBus masterEventBus, @Detail EventBus detailEventBus) {
        System.out.println("NotesController.[init] masterEventBus = " + masterEventBus + ", detailEventBus = " + detailEventBus);
        this.masterEventBus = masterEventBus;
        this.detailEventBus = detailEventBus;
        this.notesRepository = notesRepository;
    }

    @FXML
    void initialize() {
        System.out.println("NotesController.initialize()");
        notesListView.prefWidthProperty().bind(view.widthProperty());
        notesListView.setItems(data);
        notesListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Note>() {
            @Override
            public void changed(ObservableValue<? extends Note> observableValue, Note oldValue, Note newValue) {
                System.out.printf("NotesController: note selected: %s\n", newValue);
                detailEventBus.post(new NoteSelectedEvent(newValue));
            }
        });
        notesListView.setCellFactory(new Callback<ListView<Note>, ListCell<Note>>() {
            @Override
            public ListCell<Note> call(ListView<Note> locationListView) {
                return new NoteListCell();
            }
        });
        masterEventBus.register(this);
        detailEventBus.register(this);
        notesRepository.eventBus.register(this);
        deselectLocation();
        deselectNote();
    }

    private void addNotesToList(@NotNull List<Note> notes) {
        data.addAll(notes);
    }

    private void replaceNotesInList(@NotNull List<Note> notes) {
        for (Note newNote : notes) {
            for (int i = 0; i < data.size(); i++) {
                UUID id = data.get(i).getId();
                if (id != null && id.equals(newNote.getId())) {
                    data.remove(i);
                    data.add(i, newNote);
                }
            }
        }
    }

    private void removeNotesFromList(@NotNull List<UUID> idsToRemove) {
        for (UUID idToRemove : idsToRemove) {
            for (int i = 0; i < data.size(); i++) {
                UUID id = data.get(i).getId();
                if (id != null && id.equals(idToRemove)) {
                    data.remove(i);
                }
            }
        }
    }

    private void removeAllNotesFromList() {
        data.remove(0, data.size());
    }

    private class NoteListCell extends ListCell<Note> {
        @Override
        public void updateItem(Note item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                System.out.println("NoteListCell.updateItem(item = " + item);
                NoteItemController controller = new NoteItemController();
                controller.setNote(item);
                // controller.getRoot().prefWidthProperty().bind(notesListView.prefWidthProperty().subtract(15));
                controller.getRoot().maxWidthProperty().bind(notesListView.prefWidthProperty().subtract(15));
                setGraphic(controller.getRoot());
            }
        }
    }

    private void selectLocation(@NotNull Location location) {
        System.out.printf("NotesController.selectLocation location: %s\n", location);
        assert location.getId() != null : "Uninitialized location selected";
        this.location = location;
        addButton.disableProperty().setValue(false);
        view.setText(location.getName());
        removeAllNotesFromList();
        try {
            addNotesToList(Lists.newArrayList(notesRepository.findByLocationId(location.getId())));
        } catch (RepositoryException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void deselectLocation() {
        System.out.printf("NotesController.deselectLocation\n");
        deselectNote();
        this.location = null;
        addButton.disableProperty().setValue(true);
        view.setText(" ");
        removeAllNotesFromList();
    }

    private void selectNote(@NotNull Note note) {
        System.out.printf("NotesController.selectNote note: %s\n", note);
        this.note = note;
        changeButton.disableProperty().setValue(false);
        removeButton.disableProperty().setValue(false);
    }

    private void deselectNote() {
        System.out.printf("NotesController.deselectNote\n");
        this.note = null;
        changeButton.disableProperty().setValue(true);
        removeButton.disableProperty().setValue(true);
    }

    public Parent getView() {
        return view;
    }

    /******* Buttons action handlers ***********/

    @FXML
    public void handleAddButtonAction(ActionEvent event) {
        System.out.println("NotesController.handleAddButtonAction()");
        assert location != null : "Add button clicked when no location set";
        new NoteEditorController(notesRepository, null, location); // show editor for selected note
    }

    @FXML
    public void handleChangeButtonAction(ActionEvent event) {
        System.out.println("NotesController.handleChangeButtonAction()");
        List<Note> selected = notesListView.getSelectionModel().getSelectedItems();
        assert !selected.isEmpty() : "Change button clicked when nothing selected";
        assert location != null : "Change button clicked when no location set";
        new NoteEditorController(notesRepository, selected.get(0), location); // show editor for selected note
    }

    @FXML
    public void handleRemoveButtonAction(ActionEvent event) {
        System.out.println("NotesController.handleRemoveButtonAction()");
        List<Note> selected = notesListView.getSelectionModel().getSelectedItems();
        assert !selected.isEmpty() : "Remove button clicked when nothing selected";
        removeNotesFromList(Lists.transform(selected, new Function<Note, UUID>() {
            @Override
            public UUID apply(Note note) {
                return note.getId();
            }
        }));
    }

    /******** Entities selection event handlers **********/

    @Subscribe
    public void handleLocationSelectedEvent(LocationSelectedEvent event) {
        System.out.printf("NotesController.handleLocationSelectedEvent event.location = %s\n", event.location);
        if (event.location == null) {
            deselectLocation();
        } else {
            selectLocation(event.location);
        }
    }

    @Subscribe
    public void handleNoteSelectedEvent(NoteSelectedEvent event) {
        if (event.note == null) {
            deselectNote();
        } else {
            selectNote(event.note);
        }
    }

    /******** Repository changes event handlers ********/

    @Subscribe
    public void handleCreationsOccurredEvent(Repository.CreationsOccurredEvent<Note> event) {
        System.out.printf("CreationsOccurredEvent values: %s\n", event.values);
        addNotesToList(event.values);
    }

    @Subscribe
    public void handleUpdatesOccurredEvent(Repository.UpdatesOccurredEvent<Note> event) {
        System.out.printf("UpdatesOccurredEvent values: %s\n", event.values);
        replaceNotesInList(event.values);
    }

    @Subscribe
    public void handleRemovesOccurredEvent(Repository.RemovesOccurredEvent event) {
        System.out.printf("RemovesOccurredEvent ids: %s\n", event.ids);
        removeNotesFromList(event.ids);
    }

    /**************** Produced events ******************/

    public static class NoteSelectedEvent {
        public final Note note;

        NoteSelectedEvent(@Nullable Note note) {
            this.note = note;
        }
    }
}
