package data;

import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import model.Entity;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public abstract class Repository<T extends Entity> {

    public final EventBus eventBus;
    private final String storagePath;
    private static final String ext = ".bin";

    protected Repository(@NotNull EventBus repoBus, @NotNull String storagePath) {
        this.eventBus = repoBus;
        eventBus.register(this);
        this.storagePath = storagePath;
        initialize();
    }

    private void initialize() {
        try {
            Path dir = FileSystems.getDefault().getPath(storagePath);
            if (!Files.exists(dir))
                Files.createDirectory(dir);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void save(@NotNull T entity) throws RepositoryException {
        Object event;
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID());
            event = new CreationsOccurredEvent<>(entity);
        } else {
            event = new UpdatesOccurredEvent<>(entity);
        }
        String fileName = storagePath + entity.getId().toString() + ext;
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))) {
            out.writeObject(entity);
            System.out.println("Repository wrote: " + fileName);
            eventBus.post(event);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RepositoryException("Error in serialization", e);
        }
    }

    public void remove(@NotNull UUID id) throws RepositoryException {
        String fileName = storagePath + id.toString() + ext;
        try {
            if (Files.deleteIfExists(FileSystems.getDefault().getPath(fileName)))
                eventBus.post(new RemovesOccurredEvent(id));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RepositoryException("Error when removing entity with id: " + id, e);
        }
    }

    @NotNull
    public List<T> findAll() throws RepositoryException {
        List<T> result = new ArrayList<>();
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(FileSystems.getDefault().getPath(storagePath))) {
            for (Path p : ds) {
                System.out.printf("Repository.findAll found path: %s\n", p);
                if (p.toString().endsWith(ext)) {
                    try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(p))) {
                        result.add((T) in.readObject());
                    } catch (ClassCastException | ClassNotFoundException | IOException e) {
                        e.printStackTrace();
                        throw new RepositoryException("Error in all entities deserialization", e);
                    }
                }
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RepositoryException("Error in all entities deserialization", e);
        }
    }

    @NotNull
    public T findById(@NotNull UUID id) throws RepositoryException {
        String fileName = storagePath + id.toString() + ext;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName))) {
            T entity = (T) in.readObject();
            System.out.println("Repository read: " + fileName);
            return entity;
        } catch (ClassCastException e) {
            e.printStackTrace();
            throw new RepositoryException("Incompatible stored entity for fileName: " + fileName, e);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RepositoryException("No stored entity for fileName: " + fileName, e);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RepositoryException("Error in entity deserialization", e);
        }
    }


    public static class UpdatesOccurredEvent<T> {
        public final List<T> values;

        @SafeVarargs
        UpdatesOccurredEvent(T ... values) {
            this.values = Lists.newArrayList(values);
        }

        UpdatesOccurredEvent(@NotNull List<T> values) {
            this.values = values;
        }
    }

    public static class RemovesOccurredEvent {
        public final List<UUID> ids;

        RemovesOccurredEvent(UUID ... ids) {
            this.ids = Lists.newArrayList(ids);
        }

        RemovesOccurredEvent(@NotNull List<UUID> ids) {
            this.ids = ids;
        }
    }

    public static class CreationsOccurredEvent<T> {
        public final List<T> values;

        @SafeVarargs
        CreationsOccurredEvent(T ... values) {
            this.values = Lists.newArrayList(values);
        }

        CreationsOccurredEvent(@NotNull List<T> values) {
            this.values = values;
        }
    }
}
