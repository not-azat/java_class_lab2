package controller;

import app.Master;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import data.LocationsRepository;
import data.RepoBus;
import data.Repository;
import data.RepositoryException;
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
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

import static data.LocationsRepository.*;


public class LocationsController {

    @FXML private Parent view;
    @FXML private Button addButton;
    @FXML private Button changeButton;
    @FXML private Button removeButton;
    @FXML private ListView<Location> locationsListView;

    private final LocationsRepository repository;
    private final EventBus masterEventBus;
    private ObservableList<Location> data = FXCollections.observableArrayList();

    @Inject
    public LocationsController(LocationsRepository repository, @Master EventBus masterEventBus) {
        System.out.printf("LocationsController() repository: %s, repository.eventBus: %s, masterEventBus: %s\n",                repository, repository.eventBus, masterEventBus);
        this.masterEventBus = masterEventBus;
        this.repository = repository;
    }

    @FXML
    void initialize() {
        System.out.println("LocationsController.initialize()");
        try {
            data.addAll(repository.findAll());
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
        locationsListView.setItems(data);
        locationsListView.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<Location>() {
                    public void changed(ObservableValue<? extends Location> observable, Location oldValue, Location newValue) {
                        System.out.printf("LocationsController: location selected: %s\n", newValue);
                        masterEventBus.post(new LocationSelectedEvent(newValue));
                    }
                });
        locationsListView.setCellFactory(new Callback<ListView<Location>, ListCell<Location>>() {
            @Override
            public ListCell<Location> call(ListView<Location> locationListView) {
                return new LocationListCell();
            }
        });

        changeButton.disableProperty().setValue(true);
        removeButton.disableProperty().setValue(true);

        repository.eventBus.register(this);
        masterEventBus.register(this);
    }

    public Parent getView() {
        return view;
    }

    @FXML
    public void handleAddButtonAction(ActionEvent event) {
        System.out.println("LocationsController.handleAddButtonAction()");
        new LocationEditorController(repository, null); // show editor for new location
    }

    @FXML
    public void handleChangeButtonAction(ActionEvent event) {
        System.out.println("LocationsController.handleChangeButtonAction()");
        List<Location> selected = locationsListView.getSelectionModel().getSelectedItems();
        assert !selected.isEmpty() : "Change button clicked when nothing selected";
        new LocationEditorController(repository, new Location(selected.get(0))); // show editor for selected location
    }

    @FXML
    public void handleRemoveButtonAction(ActionEvent event) {
        System.out.println("LocationsController.handleRemoveButtonAction()");
        List<Location> selected = locationsListView.getSelectionModel().getSelectedItems();
        assert !selected.isEmpty() : "Remove button clicked when nothing selected";
        try {
            for (Location location : selected)
                if (location.getId() != null)
                    repository.remove(location.getId());
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
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

    private void addLocationsToList(@NotNull List<Location> locations) {
        data.addAll(locations);
    }

    private void replaceLocationsInList(@NotNull List<Location> locations) {
        for (Location newLocation : locations) {
            for (int i = 0; i < data.size(); i++) {
                UUID id = data.get(i).getId();
                if (id != null && id.equals(newLocation.getId())) {
                    data.remove(i);
                    data.add(i, newLocation);
                }
            }
        }
    }

    private void removeLocationsFromList(@NotNull List<UUID> idsToRemove) {
        for (UUID idToRemove : idsToRemove) {
            for (int i = 0; i < data.size(); i++) {
                UUID id = data.get(i).getId();
                if (id != null && id.equals(idToRemove)) {
                    data.remove(i);
                }
            }
        }
    }

    /******** Repository changes event handlers ********/

    @Subscribe
    public void handleCreationsOccurredEvent(CreationsOccurredEvent<Location> event) {
        System.out.printf("CreationsOccurredEvent values: %s\n", event.values);
        addLocationsToList(event.values);
    }

    @Subscribe
    public void handleUpdatesOccurredEvent(UpdatesOccurredEvent<Location> event) {
        System.out.printf("UpdatesOccurredEvent values: %s\n", event.values);
        replaceLocationsInList(event.values);
    }

    @Subscribe
    public void handleRemovesOccurredEvent(RemovesOccurredEvent event) {
        System.out.printf("RemovesOccurredEvent ids: %s\n", event.ids);
        removeLocationsFromList(event.ids);
    }

    @Subscribe
    public void handleLocationSelectedEvent(LocationSelectedEvent event) {
        if (event.location == null) {
            changeButton.disableProperty().setValue(true);
            removeButton.disableProperty().setValue(true);
        } else {
            changeButton.disableProperty().setValue(false);
            removeButton.disableProperty().setValue(false);
        }
    }

    /**************** Produced events ******************/

    public static class LocationSelectedEvent {
        public final Location location;

        LocationSelectedEvent(@Nullable Location location) {
            this.location = location;
        }
    }
}
