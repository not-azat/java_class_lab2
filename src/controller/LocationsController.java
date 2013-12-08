package controller;

import app.Master;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import data.*;
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
import model.Note;
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

    private final LocationsRepository locationsRepository;
    private final NotesRepository notesRepository;
    private final EventBus masterEventBus;
    private ObservableList<Location> data = FXCollections.observableArrayList();

    @Inject
    public LocationsController(LocationsRepository repository, NotesRepository notesRepository, @Master EventBus masterEventBus) {
        System.out.printf("LocationsController() locationsRepository: %s, locationsRepository.eventBus: %s, masterEventBus: %s\n",                repository, repository.eventBus, masterEventBus);
        this.masterEventBus = masterEventBus;
        this.locationsRepository = repository;
        this.notesRepository = notesRepository;
    }

    @FXML
    void initialize() {
        System.out.println("LocationsController.initialize()");
        try {
            data.addAll(locationsRepository.findAll());
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

        locationsRepository.eventBus.register(this);
        masterEventBus.register(this);
    }

    public Parent getView() {
        return view;
    }

    private void removeLocation(@NotNull Location location) {
        assert location.getId() != null : "Can't remove uninitialized location";
        try {
            for (Note note : notesRepository.findByLocationId(location.getId())) {
                notesRepository.remove(note.getId());
            }
            locationsRepository.remove(location.getId());
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    private class LocationListCell extends ListCell<Location> {
        @Override
        public void updateItem(Location item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                System.out.println("LocationListCell.updateItem(item = " + item);
                LocationItemController controller = new LocationItemController();
                controller.setLocation(item);
                controller.getRoot().maxWidthProperty().bind(locationsListView.widthProperty().subtract(20));
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
        List<UUID> selected = Lists.transform(
                Lists.newArrayList(locationsListView.getSelectionModel().getSelectedItems()),
                new Function<Location, UUID>() {
                    @Override
                    public UUID apply(Location location) {
                        return location.getId();
                    }
                });
        for (UUID idToRemove : idsToRemove) {
            for (int i = 0; i < data.size(); i++) {
                UUID id = data.get(i).getId();
                if (id != null && id.equals(idToRemove)) {
                    data.remove(i);
                    if (selected.contains(id))
                        masterEventBus.post(new LocationSelectedEvent(null));
                }
            }
        }
    }

    /********* Buttons action handlers ***************/

    @FXML
    public void handleAddButtonAction(ActionEvent event) {
        System.out.println("LocationsController.handleAddButtonAction()");
        new LocationEditorController(locationsRepository, null); // show editor for new location
    }

    @FXML
    public void handleChangeButtonAction(ActionEvent event) {
        System.out.println("LocationsController.handleChangeButtonAction()");
        List<Location> selected = locationsListView.getSelectionModel().getSelectedItems();
        assert !selected.isEmpty() : "Change button clicked when nothing selected";
        new LocationEditorController(locationsRepository, new Location(selected.get(0))); // show editor for selected location
    }

    @FXML
    public void handleRemoveButtonAction(ActionEvent event) {
        System.out.println("LocationsController.handleRemoveButtonAction()");
        List<Location> selected = locationsListView.getSelectionModel().getSelectedItems();
        assert !selected.isEmpty() : "Remove button clicked when nothing selected";
        for (Location location : selected)
            removeLocation(location);
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
