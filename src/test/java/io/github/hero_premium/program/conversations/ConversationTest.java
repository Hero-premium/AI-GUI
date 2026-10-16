package io.github.hero_premium.program.conversations;

import io.github.hero_premium.Requests;
import io.github.hero_premium.Roles;
import io.github.hero_premium.chatai.LocalAi;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConversationTest {

    private static Requests.Message msg(Roles role, String content) {
        return new Requests.Message(role, content, null, null);
    }

    private static Requests.RequestOut reply(Requests.Message message) {
        return new Requests.RequestOut("test-model", "2026-01-01T00:00:00Z", message,
                true, "stop", 0L, 0L, 0, 0, 0L, 0, 0L);
    }

    private static class FakeAi extends LocalAi {
        Requests.Message nextReply = msg(Roles.ASSISTANT, "hello");
        boolean thinking = false;
        List<Requests.Message> lastReceived;

        protected FakeAi() {
            super("FakeAi");
        }
        @Override
        public Requests.RequestOut chat(List<Requests.Message> messages) {
            lastReceived = List.copyOf(messages);
            return reply(nextReply);
        }

        @Override
        public boolean isThinking() {
            return thinking;
        }

    }

    @Test
    void chat() {
        Conversation conversation = new Conversation("normal");
        FakeAi ai = new FakeAi();
        assertNotNull(conversation.chat(List.of(msg(Roles.USER, "hi")), ai));
        assertEquals(2, conversation.getMessages().size());
        assertEquals(Roles.USER, conversation.getMessages().get(0).role());
        assertEquals(Roles.ASSISTANT, conversation.getMessages().get(1).role());
        assertEquals("hi", ai.lastReceived.getLast().content());

        conversation = new Conversation("system");
        conversation.chat(List.of(msg(Roles.SYSTEM, "sys"), msg(Roles.USER, "hi")), new FakeAi());
        assertTrue(conversation.getMessages().stream().noneMatch(m -> m.role() == Roles.SYSTEM));

        conversation = new Conversation("thinking");
        ai = new FakeAi();
        ai.thinking = true;
        assertNull(conversation.chat(List.of(msg(Roles.USER, "hi")), ai));
        assertTrue(conversation.isQueued());
        assertTrue(conversation.getMessages().isEmpty());
        assertNull(ai.lastReceived);

        conversation = new Conversation("error");
        ai = new FakeAi();
        ai.nextReply = msg(Roles.SYSTEM, "ollama server is down");
        conversation.chat(List.of(msg(Roles.USER, "hi")), ai);
        assertTrue(conversation.getMessages().stream().noneMatch(m -> m.role() == Roles.ASSISTANT));

        // multiple messages in one call (tool usage) are all stored in order
        conversation = new Conversation("multi");
        conversation.chat(List.of(msg(Roles.USER, "a"), msg(Roles.TOOL, "b")), new FakeAi());
        assertEquals("a", conversation.getMessages().get(0).content());
        assertEquals("b", conversation.getMessages().get(1).content());

        // null checks
        Conversation nullCheck = new Conversation("nulls");
        assertThrows(NullPointerException.class, () -> nullCheck.chat(null, new FakeAi()));
        assertThrows(NullPointerException.class, () -> nullCheck.chat(List.of(msg(Roles.USER, "hi")), null));
    }

    @Test
    void getMessages() {
        // empty at the start
        Conversation c = new Conversation("empty");
        assertTrue(c.getMessages().isEmpty());

        // returns a copy that can't be modified
        c.chat(List.of(msg(Roles.USER, "hi")), new FakeAi());
        List<Requests.Message> copy = c.getMessages();
        assertThrows(UnsupportedOperationException.class, () -> copy.add(msg(Roles.USER, "x")));

        // later chats don't change a copy taken earlier
        int sizeBefore = copy.size();
        c.chat(List.of(msg(Roles.USER, "again")), new FakeAi());
        assertEquals(sizeBefore, copy.size());
        assertEquals(sizeBefore + 2, c.getMessages().size());
    }

    @Test
    void isQueued() {
        // false at the start
        Conversation c = new Conversation("queued");
        assertFalse(c.isQueued());

        // true after a message is queued while thinking
        FakeAi ai = new FakeAi();
        ai.thinking = true;
        c.chat(List.of(msg(Roles.USER, "hi")), ai);
        assertTrue(c.isQueued());

        // false again after the queue is drained
        c.getNext();
        assertFalse(c.isQueued());
    }

    @Test
    void getNext() {
        // ASSUMPTION: FIFO, returns a single Message from the queued list
        Conversation c = new Conversation("next");
        FakeAi ai = new FakeAi();
        ai.thinking = true;
        c.chat(List.of(msg(Roles.USER, "first")), ai);
        c.chat(List.of(msg(Roles.USER, "second")), ai);

        assertEquals("first", c.getNext().content());
        assertEquals("second", c.getNext().content());

        // ASSUMPTION: what an empty queue does depends on QueueHelper, adjust this
        // assertNull(c.getNext());
    }
}