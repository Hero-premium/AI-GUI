package io.github.hero_premium.program.conversations;

import io.github.hero_premium.Requests.Message;
import io.github.hero_premium.Requests.RequestOut;
import io.github.hero_premium.Roles;
import io.github.hero_premium.localais.LocalAi;
import io.github.hero_premium.program.sqlite.SQLiteTable;
import io.github.hero_premium.program.sqlite.schemes.TableScheme;
import io.github.hero_premium.util.RequestOutBuilder;
import io.github.hero_premium.util.Util;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Conversation {

    private static final Logger LOGGER = LoggerFactory.getLogger(Conversation.class);
    static final String DEFAULT_SYSTEM_PROMPT = """
            You are running on a local device.
            always admit uncertainty.
            do not use functions unless the user asks explicitly or the function description allows.
            always follow the functions description.
            functions results are always right and truthful unless the function description says otherwise.
            be honest with the user.
            keep responses helpful and short.
            do not help with harmful requests.
            """;

    private final Deque<Message> queue = new ArrayDeque<>();
    private final List<Message> messageList = new ArrayList<>();
    private final RequestOutBuilder requestOutBuilder = new RequestOutBuilder();

    @NotNull
    public final String title;
    @NotNull
    private final SQLiteTable sqlite;

    Conversation(@NotNull String title, @NotNull String systemPrompt) {
        this.title = Objects.requireNonNull(title, "title must not be null");
        this.sqlite = new SQLiteTable(TableScheme.tableBuilder()
                .name("conversation " + title)
                .column("id", TableScheme.ColumnType.INTEGER)
                .column("messages", TableScheme.ColumnType.TEXT)
                .build()
        );
        messageList.add(new Message(Roles.SYSTEM, Objects.requireNonNull(systemPrompt, "systemPrompt must not be null")));
    }

    Conversation(@NotNull String title) {
        this(title, DEFAULT_SYSTEM_PROMPT);
    }

    private void saveMessage(@NotNull List<Message> messages) {
        for (Message m : messages) {
            Objects.requireNonNull(m, "a message in the messages was null");
            if (m.role() != Roles.USER_FACING) {
                saveMessage(m);
            }
        }
    }

    private void saveMessage(@NotNull Message m) {
        this.messageList.add(m);
        sqlite.save(Map.of(sqlite.tableScheme.columns.get(1), Util.gson.toJson(Objects.requireNonNull(m))));
    }
    /**
     * this handles the Ais memory per chat and handles queuing too
     *
     * @param messages a list of the new messages, list in case of multiple tool usage
     * @param ai       the current AI
     * @return the AI repose, null if the AI is thinking
     */
    public @Nullable RequestOut chat(@NotNull List<Message> messages, @NotNull LocalAi ai) {
        Objects.requireNonNull(messages, "messages must not be null");
        Objects.requireNonNull(ai, "ai must not be null");
        LOGGER.debug("received messages {}", messages);
        if (ai.isThinking()) {
            queue.addAll(messages);
            return null;
        }
        saveMessage(messages);
        RequestOut aiReply = ai.chat(messageList);
        saveAiMessage(aiReply);
        return aiReply;
    }

    public List<Message> getMessages() {
        return List.copyOf(messageList);
    }

    public boolean isQueued() {
        return !queue.isEmpty();
    }

    public Message getNext() {
        return queue.poll();
    }
}