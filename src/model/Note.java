package model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.UUID;


public class Note extends Entity {

    private String text;
    private UUID locationId;

    public Note(@NotNull String text, @NotNull UUID locationId) {
        this.text = text;
        this.locationId = locationId;
    }

    public Note(@NotNull Note other) {
        super(other.getId());
        this.text = other.text;
        this.locationId = other.locationId;
    }

    @NotNull public String getText() {
        return text;
    }

    public void setText(@NotNull String text) {
        this.text = text;
    }

    @NotNull public UUID getLocationId() {
        return locationId;
    }

    public void setLocationId(@NotNull UUID locationId) {
        this.locationId = locationId;
    }

}
