package model;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;


public class Note implements Serializable {
    private final Integer id;
    private String text;
    private Integer locationId;

    public Note(@NotNull Integer id, @NotNull String text, @NotNull Integer locationId) {
        this.id = id;
        this.text = text;
        this.locationId = locationId;
    }

    @NotNull public Integer getId() {
        return id;
    }

    @NotNull public String getText() {
        return text;
    }

    public void setText(@NotNull String text) {
        this.text = text;
    }

    @NotNull public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(@NotNull Integer locationId) {
        this.locationId = locationId;
    }

}
