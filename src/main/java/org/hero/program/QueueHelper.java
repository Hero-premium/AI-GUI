package org.hero.program;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class QueueHelper {

    private final List<String> inQueue = new ArrayList<>();

    /**
     *
     * @return true if something is still in queue
     */
    public boolean isQueued() {
        return !inQueue.isEmpty();
    }


    /**
     * removes the earlies message in the queue then returns it
     *
     * @return the earlies option in the queue, null if nothing was queued, call {@link #isQueued()} to be null safe
     * @see #isQueued()
     */
    public String getNext() {
        if (!inQueue.isEmpty()) return inQueue.removeFirst();
        return null;
    }


    public void addToQueue(String message) {
        inQueue.add(Objects.requireNonNull(message, "message must not be null"));
    }
}
