package io.github.hero_premium.program.conversations;

import io.github.hero_premium.Requests;
import io.github.hero_premium.Roles;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QueueHelperTest {

    @Test
    void isQueued() {
        final QueueHelper queueHelper = new QueueHelper();
        assertFalse(queueHelper.isQueued());
        queueHelper.addToQueue(Collections.singletonList(new Requests.Message(Roles.SYSTEM, "test")));
        assertTrue(queueHelper.isQueued());
    }

    @Test
    void getNext() {
        final QueueHelper queueHelper = new QueueHelper();
        assertNull(queueHelper.getNext());
        List<Requests.Message> messages = Collections.singletonList(new Requests.Message(Roles.SYSTEM, "test"));
        queueHelper.addToQueue(messages);
        assertEquals(messages.getFirst(), queueHelper.getNext());
        assertNull(queueHelper.getNext());
    }

    @Test
    void addToQueue() {
        final QueueHelper queueHelper = new QueueHelper();
        queueHelper.addToQueue(Collections.singletonList(new Requests.Message(Roles.SYSTEM, "test")));
        assertTrue(queueHelper.isQueued());
        assertThrows(NullPointerException.class, () -> queueHelper.addToQueue(null));
    }
}