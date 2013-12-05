package data;


import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import model.Note;
import org.jetbrains.annotations.NotNull;

public class NotesRepository extends Repository<Note> {

    @Inject
    public NotesRepository(@RepoBus("Note") EventBus repoBus) {
        super(repoBus, "notes/");
    }
}
