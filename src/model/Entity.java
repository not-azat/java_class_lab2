package model;

import data.AlreadyInitializedException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.UUID;


public abstract class Entity implements Serializable {

    private UUID id;

    protected Entity() {}

    protected Entity(@Nullable UUID id) {
        this.id = id;
    }

    @Nullable public UUID getId() {
        return id;
    }

    public void setId(@NotNull UUID id) throws AlreadyInitializedException {
        if (this.id != null)
            throw new AlreadyInitializedException();
        this.id = id;
    }
}
