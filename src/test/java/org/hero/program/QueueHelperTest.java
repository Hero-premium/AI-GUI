package org.hero.program;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QueueHelperTest {

    @Test
    void isQueued() {
        final QueueHelper queueHelper = new QueueHelper();
        assertFalse(queueHelper.isQueued());
        queueHelper.addToQueue("test");
        assertTrue(queueHelper.isQueued());
    }

    @Test
    void getNext() {
        final QueueHelper queueHelper = new QueueHelper();
        assertNull(queueHelper.getNext());
        queueHelper.addToQueue("test");
        assertEquals("test", queueHelper.getNext());
        assertNull(queueHelper.getNext());
    }

    @Test
    void addToQueue() {
        final QueueHelper queueHelper = new QueueHelper();
        queueHelper.addToQueue("test");
        assertTrue(queueHelper.isQueued());
    }
}