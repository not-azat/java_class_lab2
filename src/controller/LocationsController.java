package controller;

import app.Master;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import data.RepoBus;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import model.Location;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static data.LocationsRepository.*;


public class LocationsController {

    @FXML private Parent view;
    @FXML private Button addButton;
    @FXML private Button changeButton;
    @FXML private Button removeButton;
    @FXML private ListView<Location> locationsListView;

    private final EventBus masterEventBus;
    private final EventBus locationRepoBus;
    private ObservableList<Location> data = FXCollections.observableArrayList(
            new Location(1, "first", 1.0, 2.2345, Lists.newArrayList(1, 2, 3)),
            new Location(2, "second", 2.123, 4.4312313, Lists.newArrayList(4, 5, 6)));

    @Inject
    public LocationsController(@Master EventBus masterEventBus, @RepoBus("Location") EventBus repoBus) {
        System.out.println("LocationsController.[init] masterEventBus = " + masterEventBus);
        this.masterEventBus = masterEventBus;
        this.locationRepoBus = repoBus;
    }

    @FXML
    void initialize() {
        System.out.println("LocationsController.initialize()");

        locationsListView.setItems(data);
        locationsListView.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<Location>() {
                    public void changed(ObservableValue<? extends Location> observable, Location oldValue, Location newValue) {
                        System.out.println("LocationsController: item selected id = " + newValue.getId());
                        masterEventBus.post(new LocationSelectedEvent(newValue));
                    }
                });
        locationsListView.setCellFactory(new Callback<ListView<Location>, ListCell<Location>>() {
            @Override
            public ListCell<Location> call(ListView<Location> locationListView) {
                return new LocationListCell();
            }
        });

        changeButton.disableProperty().setValue(false);
        removeButton.disableProperty().setValue(false);

        locationRepoBus.register(this);
    }

    @Subscribe
    private void handleLocationSelectedEvent(LocationSelectedEvent event) {
        changeButton.disableProperty().setValue(true);
        removeButton.disableProperty().setValue(true);
    }

    @Subscribe
    private void handleRepoUpdate(UpdatesOccurredEvent event) {
        System.out.println("LocationsController UpdatesOccurredEvent");
    }

    public Parent getView() {
        return view;
    }

    @FXML
    public void handleAddButtonAction(ActionEvent event) {
        System.out.println("LocationsController.handleAddButtonAction()");
        //masterEventBus.post(new AddButtonClickedEvent());
        new LocationEditorController(masterEventBus, null); // show editor for new location
    }

    @FXML
    public void handleChangeButtonAction(ActionEvent event) {
        System.out.println("LocationsController.handleChangeButtonAction()");
        //masterEventBus.post(new ChangeButtonClickedEvent());
        List<Location> selected = locationsListView.getSelectionModel().getSelectedItems();
        assert !selected.isEmpty() : "Change button clicked when nothing selected";
        new LocationEditorController(masterEventBus, selected.get(0)); // show editor for selected location
    }

    @FXML
    public void handleRemoveButtonAction(ActionEvent event) {
        System.out.println("LocationsController.handleRemoveButtonAction()");
        masterEventBus.post(new RemoveButtonClickedEvent());
        List<Location> selected = locationsListView.getSelectionModel().getSelectedItems();
        assert !selected.isEmpty() : "Remove button clicked when nothing selected";
    }

    private class LocationListCell extends ListCell<Location> {
        @Override
        public void updateItem(Location item, boolean empty) {
            System.out.println("LocationListCell.updateItem(item = " + item + ", empty = " + empty);
            super.updateItem(item, empty);
            if (item != null) {
                LocationItemController controller = new LocationItemController(masterEventBus);
                controller.setLocation(item);
                setGraphic(controller.getRoot());
            }
        }
    }

    private static class AddButtonClickedEvent {}

    private static class ChangeButtonClickedEvent {}

    private static class RemoveButtonClickedEvent {}

    static class LocationSelectedEvent {
        public final Location location;

        LocationSelectedEvent(@NotNull Location location) {
            this.location = location;
        }
    }
}
