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

    public Location(@NotNull String name, @NotNull Double latitude,
                    @NotNull Double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Location(@NotNull Location other) {
        super(other.getId());
        this.name = other.name;
        this.latitude = other.latitude;
        this.longitude = other.longitude;
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

    @Override
    public String toString() {
        return String.format("Location[id: %s, name: %s, latitude: %f, longitude: %f]",
                getId(), name, latitude, longitude);
    }

}
