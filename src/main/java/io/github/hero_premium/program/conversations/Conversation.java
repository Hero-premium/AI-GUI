package io.github.hero_premium.program.conversations;

import io.github.hero_premium.Requests;
import io.github.hero_premium.Roles;
import io.github.hero_premium.chatai.LocalAi;
import io.github.hero_premium.program.sqlite.SQLite;
import io.github.hero_premium.program.sqlite.SQLiteData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Conversation {
    private static final SQLite sqlite = new SQLite(new SQLiteData.Table(
            "conversations",
            new SQLiteData.Column("id", SQLiteData.ColumnType.INTEGER),
            new SQLiteData.Column("messages", SQLiteData.ColumnType.TEXT)
    ));
    private static final Logger LOGGER = LoggerFactory.getLogger(Conversation.class);
    static final String DEFAULT_SYSTEM_PROMPT = "You are running inside a local chat app.";

    private final Deque<Requests.Message> queue = new ArrayDeque<>();
    private final List<Requests.Message> messageList = new ArrayList<>();
    public final String title;

    Conversation(String title, String systemPrompt) {
        this.title = Objects.requireNonNull(title, "title must not be null");
        messageList.add(new Requests.Message(Roles.SYSTEM, systemPrompt));
    }

    Conversation(String title) {
        this(title, DEFAULT_SYSTEM_PROMPT);
    }

    private void saveMessage(@NotNull List<Requests.Message> messages) {
        for (Requests.Message m : messages) {
            Objects.requireNonNull(m, "a message in the messages was null");
            if (m.role() != Roles.SYSTEM) {
                this.messageList.add(m);
                sqlite.getClass(); // add to database here 😭
            }
        }
    }

    /**
     * this handles the Ais memory per chat and handles queuing too
     *
     * @param messages a list of the new messages, list in case of multiple tool usage
     * @param ai       the current AI
     * @return the AI repose, null if the AI is thinking
     */
    public Requests.@Nullable RequestOut chat(@NotNull List<Requests.Message> messages, @NotNull LocalAi ai) {
        Objects.requireNonNull(messages, "messages must not be null");
        Objects.requireNonNull(ai, "ai must not be null");
        LOGGER.debug("received message {}", messages.getLast());
        if (ai.isThinking()) {
            queue.addAll(messages);
            return null;
        }
        saveMessage(messages);
        Requests.RequestOut requestOut = ai.chat(this.messageList);
        saveMessage(List.of(requestOut.message()));
        return requestOut;
    }

    public List<Requests.Message> getMessages() {
        return List.copyOf(messageList);
    }

    public boolean isQueued() {
        return !queue.isEmpty();
    }

    public Requests.Message getNext() {
        return queue.poll();
    }
}