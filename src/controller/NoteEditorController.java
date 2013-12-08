package controller;

import data.LocationsRepository;
import data.NotesRepository;
import data.RepositoryException;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import model.Location;
import model.Note;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

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

    private final NotesRepository notesRepository;
    @NotNull private Note note;
    @NotNull private Location location;

    public NoteEditorController(@NotNull NotesRepository notesRepository,
                                @Nullable Note note,
                                @NotNull Location location) {
        assert location.getId() != null : "Uninitialized location passed to NoteEditor";
        this.notesRepository = notesRepository;        this.note = (note == null
                ? new Note("", location.getId())
                : note);
        this.location = location;
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
        noteTextArea.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String s2) {
            note.setText(s2);
            }
        });
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
            notesRepository.save(note);
            stage.hide();
        } catch (ValidationException | RepositoryException e) {
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

    private static class ValidationException extends Exception{
        ValidationException(String message) {
            super(message);
        }
    }
}
