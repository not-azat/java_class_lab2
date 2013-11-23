package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.io.IOException;
import java.util.Random;

public class LocationsController {

    @FXML private Parent view;
    @FXML private Button addButton;
    @FXML private Button changeButton;
    @FXML private Button removeButton;
    @FXML private ListView<String> locationsListView;

    ObservableList<String> data = FXCollections.observableArrayList(
            "chocolate", "salmon", "gold", "coral", "darkorchid",
            "darkgoldenrod", "lightsalmon", "black", "rosybrown", "blue",
            "blueviolet", "brown");

    @FXML
    void initialize() {
        System.out.println("LocationsController.initialize()");

        locationsListView.setItems(data);
        locationsListView.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> stringListView) {
                return new ColorRectCell();
            }
        });
    }

    public Parent getView() {
        return view;
    }

    @FXML
    public void handleAddButtonAction(ActionEvent event) {
        System.out.println("LocationsController.handleAddButtonAction()");
    }

    @FXML
    public void handleChangeButtonAction(ActionEvent event) {
        System.out.println("LocationsController.handleChangeButtonAction()");
    }

    @FXML
    public void handleRemoveButtonAction(ActionEvent event) {
        System.out.println("LocationsController.handleRemoveButtonAction()");
    }

    private static class ColorRectCell extends ListCell<String> {
        @Override
        public void updateItem(String item, boolean empty) {
            System.out.println("ColorRectCell.updateItem(item:  " + item + ", empty: " + empty);
            super.updateItem(item, empty);
            if (item != null) {
                LocationItemController controller = new LocationItemController();
                controller.setInfo("Some name #" + new Random(System.currentTimeMillis()).nextInt(), 0d, 0d);
                setGraphic(controller.getView());
            }
        }
    }
}
