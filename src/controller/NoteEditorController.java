package controller;

import com.google.common.eventbus.EventBus;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Location;
import model.Note;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;

/**
 * User: azat, Date: 03.12.13, Time: 0:31
 */
public class NoteEditorController {

    @FXML private Parent view;
    @FXML private Label locationName;
    @FXML private TextArea noteTextArea;
    @FXML private Button cancelButton;
    @FXML private Button saveButton;
    @FXML private Label errorLabel;
    private Stage stage;

    private final EventBus eventBus;

    @NotNull private Note note;
    @NotNull private Location location;

    public NoteEditorController(@NotNull EventBus eventBus, @Nullable Note note, @NotNull Location location) {
        assert location.getId() != null : "Uninitialized location passed to NoteEditor";
        this.note = (note == null
                ? new Note("", location.getId())
                : note);
        this.location = location;
        this.eventBus = eventBus;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/noteEditor.fxml"));
            fxmlLoader.setController(this);
            Parent root = (Parent) fxmlLoader.load();
            this.stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Редактирование");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize() {
        System.out.println("LocationEditorController.initialize()");
        noteTextArea.setText(note.getText());
        locationName.setText(location.getName());
    }

    @FXML
    public void handleCancelButtonAction(ActionEvent event) {
        System.out.println("NoteEditorController.handleCancelButtonClicked");
        stage.hide();
    }

    @FXML
    public void handleSaveButtonAction(ActionEvent event) {
        System.out.println("NoteEditorController.handleSaveButtonClicked");
        try {
            validateNote(note);
            eventBus.post(new CreateNoteEvent(note));
            stage.hide();
        } catch (ValidationException e) {
            showError(e.getMessage());
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
    }

    private static void validateNote(Note note) throws ValidationException {
        if ("".equals(note.getText()))
            throw new ValidationException("Пустая заметка!");
    }

    public static class CreateNoteEvent {
        public final Note note;

        CreateNoteEvent(@NotNull Note note) {
            this.note = note;
        }
    }

    private static class ValidationException extends Exception{
        ValidationException(String message) {
            super(message);
        }
    }
}
