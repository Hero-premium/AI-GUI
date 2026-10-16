package io.github.hero_premium.program.conversations;

import io.github.hero_premium.Requests.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class QueueHelper {

    private final List<Message> inQueue = new ArrayList<>();

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
    public Message getNext() {
        if (!inQueue.isEmpty()) return inQueue.removeFirst();
        return null;
    }


    public void addToQueue(List<Message> messages) {
        inQueue.addAll(Objects.requireNonNull(messages, "messages must not be null"));
    }
}
