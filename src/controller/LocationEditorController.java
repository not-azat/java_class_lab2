package controller;


import com.google.common.eventbus.EventBus;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;

public class LocationEditorController {

    @FXML private Parent view;
    @FXML private TextField nameTextField;
    @FXML private TextField latitudeTextField;
    @FXML private TextField longitudeTextField;
    @FXML private Button cancelButton;
    @FXML private Button saveButton;
    @FXML private Label errorLabel;
    private Stage stage;

    private final EventBus eventBus;
    @NotNull private Location location;

    public LocationEditorController(@NotNull EventBus eventBus, @Nullable Location location) {
        this.location = (location == null
                ? new Location(null, "", 0.0, 0.0, new ArrayList<Integer>())
                : location);
        this.eventBus = eventBus;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/locationEditor.fxml"));
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
        nameTextField.setText(location.getName());
        latitudeTextField.setText(location.getLatitude().toString());
        longitudeTextField.setText(location.getLongitude().toString());
    }

    @FXML
    public void handleCancelButtonAction(ActionEvent event) {
        System.out.println("LocationEditorController.handleCancelButtonClicked");
        stage.hide();
    }

    @FXML
    public void handleSaveButtonAction(ActionEvent event) {
        System.out.println("LocationEditorController.handleSaveButtonClicked");
        try {
            validateLocation(location);
            eventBus.post(new CreateLocationEvent(location));
            stage.hide();
        } catch (ValidationException e) {
            showError(e.getMessage());
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
    }

    private static void validateLocation(Location location) throws ValidationException {
        if ("".equals(location.getName()))
            throw new ValidationException("Имя не может быть пустым");
    }

    public static class CreateLocationEvent {
        public final Location location;

        CreateLocationEvent(@NotNull Location location) {
            this.location = location;
        }
    }

    private static class ValidationException extends Exception{
        ValidationException(String message) {
            super(message);
        }
    }
}
