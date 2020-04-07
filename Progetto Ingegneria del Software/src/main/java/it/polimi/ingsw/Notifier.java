package it.polimi.ingsw;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Notifier<T> {
    private final Map<Object, Consumer<T>> callbacks;
    private boolean iterating;

    public Notifier() {
        this.callbacks = new HashMap<>();
        this.iterating = false;
    }

    public void addListener(Object key, Consumer<T> callback) {
        if(this.iterating) {
            throw new RuntimeException("Adding items when iterating could be problematic, not implemented yet");
        }
        this.callbacks.put(key, callback);
    }

    public void removeListener(Object key) {
        if(this.iterating) {
            throw new RuntimeException("Removing items when iterating could be problematic, not implemented yet");
        }
        this.callbacks.remove(key);
    }

    public void notify(T value) {
        try {
            this.iterating = true;
            for (Consumer<T> callback : this.callbacks.values()) {
                callback.accept(value);
            }
        }
        finally {
            this.iterating = false;
        }
    }
}
