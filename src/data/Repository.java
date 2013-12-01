package data;

import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public abstract class Repository<T extends Serializable> {

    private final EventBus bus;
    private final String storagePath;

    protected Repository(@NotNull EventBus repoBus, @NotNull String storagePath) {
        this.bus = repoBus;
        bus.register(this);
        this.storagePath = storagePath;
    }

    @NotNull
    protected abstract String fileNameFor(@NotNull T entity);

    @Subscribe
    private void handleLoadAllEvent(LoadAllEvent event) {
        System.out.println("Repository LoadAllEvent");
        try {
            List<T> entities = readAll();
            bus.post(new LoadAllReplyEvent<>(entities, null));
        } catch (RepositoryException e) {
            bus.post(new LoadAllReplyEvent<>(null, e));
            System.out.println(e);
        }
    }

    @Subscribe
    private void handleLoadByIdEvent(LoadByIdEvent<T> event) {
        System.out.println("Repository LoadByIdEvent id: " + event.id);
        try {
            T entity = readEntity(event.id.toString());
            bus.post(new LoadByIdReplyEvent<>(event.id, entity, null));
        } catch (RepositoryException e) {
            bus.post(new LoadByIdReplyEvent<>(event.id, null, e));
            System.out.println(e);
        }
    }

    @Subscribe
    private void handleUpdateEvent(PostUpdateEvent<T> event) {
        System.out.println("Repository PostUpdateEvent value: " + event.value);
        try {
            writeEntity(event.value, fileNameFor(event.value));
            bus.post(new UpdatesOccurredEvent<>(event.value));
        } catch (RepositoryException e) {
            System.out.println(e);
        }
    }

    private void writeEntity(@NotNull T entity, @NotNull String fileName) throws RepositoryException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(storagePath + fileName))) {
            out.writeObject(entity);
            System.out.println("Repository wrote: " + storagePath + fileName);
        } catch (IOException e) {
            throw new RepositoryException("Error in serialization", e);
        }
    }

    @NotNull
    private List<T> readAll() throws RepositoryException {
        List<T> result = new ArrayList<T>();
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(FileSystems.getDefault().getPath(storagePath))) {
            for (Path p : ds) {
                try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(p))) {
                    result.add((T)in.readObject());
                } catch (ClassNotFoundException | IOException e) {
                    throw new RepositoryException("Error in all entities deserialization", e);
                }
            }
            return result;
        } catch (IOException e) {
            throw new RepositoryException("Error in all entities deserialization", e);
        }
    }

    @NotNull
    private T readEntity(@NotNull String fileName) throws RepositoryException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(storagePath + fileName))) {
            T entity = (T)in.readObject();
            System.out.println("Repository read: " + storagePath + fileName);
            return entity;
        } catch (FileNotFoundException e) {
            throw new RepositoryException("No stored entity for fileName: " + fileName, e);
        } catch (IOException | ClassNotFoundException e) {
            throw new RepositoryException("Error in entity deserialization", e);
        }
    }


    public static class LoadAllEvent {}

    public static class LoadAllReplyEvent<T> {
        public final Exception exception;
        public final List<T> values;

        LoadAllReplyEvent(@Nullable List<T> values, @Nullable Exception exception) {
            this.values = values;
            this.exception = exception;
        }
    }

    public static class LoadByIdEvent<T> {
        public final Integer id;

        public LoadByIdEvent(@NotNull Integer id) {
            this.id = id;
        }
    }

    public static class LoadByIdReplyEvent<T> {
        public final Integer id;
        public final Exception exception;
        public final T value;

        LoadByIdReplyEvent(@NotNull Integer id, @Nullable T value, @Nullable Exception exception) {
            this.id = id;
            this.value = value;
            this.exception = exception;
        }
    }

    public static class PostUpdateEvent<T> {
        public final T value;

        public PostUpdateEvent(@NotNull T value) {
            this.value = value;
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
}
