package model;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.List;


public class Location implements Serializable {

    private final Integer id;
    private String name;
    private Double latitude;
    private Double longitude;
    private List<Integer> noteIds;

    public Location(@NotNull Integer id, @NotNull String name, @NotNull Double latitude,
                    @NotNull Double longitude, @NotNull List<Integer> noteIds) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.noteIds = noteIds;
    }

    @NotNull public Integer getId() {
        return id;
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

    @NotNull public List<Integer> getNoteIds() {
        return noteIds;
    }

    public void setNoteIds(@NotNull List<Integer> noteIds) {
        this.noteIds = noteIds;
    }

}
