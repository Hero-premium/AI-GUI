package io.github.hero_premium.program.conversations;

import io.github.hero_premium.Requests;
import io.github.hero_premium.Role;
import io.github.hero_premium.localais.LocalAi;
import io.github.hero_premium.program.sqlite.Constants;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

class ConversationTest {

    private static Requests.Message msg(Role role, String content) {
        return new Requests.Message(role, content);
    }

    private static Requests.RequestOut reply(Requests.Message message) {
        return new Requests.RequestOut(
                "test-model", "2026-01-01T00:00:00Z", message,
                true, "stop", 0L, 0L, 0, 0, 0L, 0, 0L
        );
    }

    private static class FakeAi extends LocalAi {
        Requests.Message nextReply = msg(Role.ASSISTANT, "hello");
        boolean thinking = false;
        List<Requests.Message> lastReceived;

        protected FakeAi() {
            super("FakeAi");
        }

        @Override
        public void chat(List<Requests.Message> messages, Consumer<Requests.RequestOut> out) {
            lastReceived = List.copyOf(messages);
            out.accept(reply(nextReply));
        }

        @Override
        public boolean isBusy() {
            return thinking;
        }
    }

    @Test
    void chat() {
        Conversation conversation = new Conversation("normal", UUID.randomUUID(), Constants.TO_MEMORY_URL);
        FakeAi ai = new FakeAi();

        conversation.chat(List.of(msg(Role.USER, "hi")), ai, _ -> {});

        IO.println(conversation.getMessages());
        assertEquals(3, conversation.getMessages().size());
        assertEquals(Role.SYSTEM, conversation.getMessages().get(0).role());
        assertEquals(Role.USER, conversation.getMessages().get(1).role());
        assertEquals(Role.ASSISTANT, conversation.getMessages().get(2).role());
        assertEquals("hi", ai.lastReceived.getLast().content());

        conversation = new Conversation("system", UUID.randomUUID(), Constants.TO_MEMORY_URL);
        conversation.chat(
                List.of(msg(Role.USER_FACING, "sys"), msg(Role.USER, "hi")),
                new FakeAi(),
                _ -> {}
        );

        assertTrue(conversation.getMessages().stream()
                .noneMatch(m -> m.role() == Role.USER_FACING));

        conversation = new Conversation("thinking", UUID.randomUUID(), Constants.TO_MEMORY_URL);
        ai = new FakeAi();
        ai.thinking = true;

        conversation.chat(List.of(msg(Role.USER, "hi")), ai, _ -> {});

        assertTrue(conversation.isQueued());
        assertEquals(1, conversation.getMessages().size());
        assertNull(ai.lastReceived);

        conversation = new Conversation("error", UUID.randomUUID(), Constants.TO_MEMORY_URL);
        ai = new FakeAi();
        ai.nextReply = msg(Role.USER_FACING, "ollama server is down");

        conversation.chat(List.of(msg(Role.USER, "hi")), ai, _ -> {});

        assertTrue(conversation.getMessages().stream()
                .noneMatch(m -> m.role() == Role.ASSISTANT));

        conversation = new Conversation("multi", UUID.randomUUID(), Constants.TO_MEMORY_URL);

        conversation.chat(
                List.of(msg(Role.USER, "a"), msg(Role.TOOL, "b")),
                new FakeAi(),
                _ -> {}
        );

        assertEquals(
                Conversation.DEFAULT_SYSTEM_PROMPT,
                conversation.getMessages().getFirst().content()
        );
        assertEquals("a", conversation.getMessages().get(1).content());
        assertEquals("b", conversation.getMessages().get(2).content());

        Conversation nullCheck = new Conversation("nulls", UUID.randomUUID(), Constants.TO_MEMORY_URL);

        assertThrows(
                NullPointerException.class,
                () -> nullCheck.chat((List<Requests.Message>) null, new FakeAi(), _ -> {})
        );
        assertThrows(
                NullPointerException.class,
                () -> nullCheck.chat(
                        List.of(msg(Role.USER, "hi")),
                        null,
                        _ -> {}
                )
        );
    }

    @Test
    void getMessages() {
        Conversation c = new Conversation("empty", UUID.randomUUID(), Constants.TO_MEMORY_URL);

        assertEquals(1, c.getMessages().size());

        c.chat(List.of(msg(Role.USER, "hi")), new FakeAi(), _ -> {});

        List<Requests.Message> copy = c.getMessages();

        assertThrows(
                UnsupportedOperationException.class,
                () -> copy.add(msg(Role.USER, "x"))
        );

        int sizeBefore = copy.size();

        c.chat(List.of(msg(Role.USER, "again")), new FakeAi(), _ -> {});

        assertEquals(sizeBefore, copy.size());
        assertEquals(sizeBefore + 2, c.getMessages().size());
    }

    @Test
    void isQueued() {
        Conversation c = new Conversation("queued", UUID.randomUUID(), Constants.TO_MEMORY_URL);

        assertFalse(c.isQueued());

        FakeAi ai = new FakeAi();
        ai.thinking = true;

        c.chat(List.of(msg(Role.USER, "hi")), ai, _ -> {});

        assertTrue(c.isQueued());

        c.getNext();

        assertFalse(c.isQueued());
    }

    @Test
    void getNext() {
        Conversation c = new Conversation("next", UUID.randomUUID(), Constants.TO_MEMORY_URL);
        FakeAi ai = new FakeAi();
        ai.thinking = true;

        c.chat(List.of(msg(Role.USER, "first")), ai, _ -> {});
        c.chat(List.of(msg(Role.USER, "second")), ai, _ -> {});

        assertEquals("first", c.getNext().content());
        assertEquals("second", c.getNext().content());

        assertNull(c.getNext());
    }
}