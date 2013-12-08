package data;


import com.google.common.base.Predicate;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import model.Note;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static com.google.common.collect.Iterables.filter;

public class NotesRepository extends Repository<Note> {

    @Inject
    public NotesRepository(@RepoBus("Note") EventBus repoBus) {
        super(repoBus, "notes/");
    }

    public Iterable<Note> findByLocationId(@NotNull final UUID locationId) throws RepositoryException {
        return filter(findAll(), new Predicate<Note>() {
            @Override
            public boolean apply(Note note) {
                return locationId.equals(note.getLocationId());
            }
        });
    }
}
