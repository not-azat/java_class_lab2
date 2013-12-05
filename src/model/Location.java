package model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class Location extends Entity {

    private String name;
    private Double latitude;
    private Double longitude;
    private List<UUID> noteIds;

    public Location(@NotNull String name, @NotNull Double latitude,
                    @NotNull Double longitude, @NotNull List<UUID> noteIds) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.noteIds = noteIds;
    }

    public Location(@NotNull Location other) {
        super(other.getId());
        this.name = other.name;
        this.latitude = other.latitude;
        this.longitude = other.longitude;
        this.noteIds = new ArrayList<>(other.noteIds);
    }

    @NotNull public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    @NotNull public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(@NotNull Double latitude) {
        this.latitude = latitude;
    }

    @NotNull public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(@NotNull Double longitude) {
        this.longitude = longitude;
    }

    @NotNull public List<UUID> getNoteIds() {
        return noteIds;
    }

    public void setNoteIds(@NotNull List<UUID> noteIds) {
        this.noteIds = noteIds;
    }

    @Override
    public String toString() {
        return String.format("Location[id: %s, name: %s, latitude: %f, longitude: %f, noteIds: %s",
                getId(), name, latitude, longitude, noteIds);
    }

}
