package app;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import data.RepoBus;

public class ApplicationModule extends AbstractModule {

    @Override
    protected void configure() {}

    /** For events related to locations UI */
    @Provides @Master @Singleton
    EventBus provideMasterEventBus() {
        EventBus instance = new EventBus("Master EventBus");
        instance.register(new Block<DeadEvent>() {
            @Subscribe public void run(DeadEvent e) {
                System.out.println("Master EventBus: DeadEvent: " + e.getEvent() + " from: " + e.getSource());
            }
        });
        return instance;
    }

    /** For events related to notes UI */
    @Provides @Detail @Singleton
    EventBus provideDetailEventBus() {
        EventBus instance = new EventBus("Detail EventBus");
        instance.register(new Block<DeadEvent>() {
            @Subscribe public void run(DeadEvent e) {
                System.out.println("Detail EventBus: DeadEvent: " + e.getEvent() + " from: " + e.getSource());
            }
        });
        return instance;
    }

    @Provides @RepoBus("Location") @Singleton
    EventBus provideLocationEventBus() {
        EventBus instance = new EventBus("Location Repository EventBus");
        instance.register(new Block<DeadEvent>() {
            @Subscribe public void run(DeadEvent e) {
                System.out.println("Location Repository EventBus: DeadEvent: " + e.getEvent() + " from: " + e.getSource());
            }
        });
        return instance;
    }

    @Provides @RepoBus("Note") @Singleton
    EventBus provideNoteEventBus() {
        EventBus instance = new EventBus("Note Repository EventBus");
        instance.register(new Block<DeadEvent>() {
            @Subscribe public void run(DeadEvent e) {
                System.out.println("Note Repository EventBus: DeadEvent: " + e.getEvent() + " from: " + e.getSource());
            }
        });
        return instance;
    }

    private static interface Block<T> {
        void run(T arg);
    }
}
