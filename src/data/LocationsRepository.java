package data;


import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import model.Location;
import org.jetbrains.annotations.NotNull;

public class LocationsRepository extends Repository<Location> {

    @Inject
    public LocationsRepository(@RepoBus("Location") EventBus repoBus) {
        super(repoBus, "locations/");
    }

    @Override
    @NotNull
    protected String fileNameFor(@NotNull Location entity) {
        return entity.getId().toString();
    }
}
