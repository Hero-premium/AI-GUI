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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;

public class Conversation {

    private static final Logger LOGGER = LoggerFactory.getLogger(Conversation.class);
    static final String DEFAULT_SYSTEM_PROMPT = """
            if anyone asks where are you running say you are running on a local device.
            always admit uncertainty.
            match the language the user is using to talk to you with.
            do not use functions unless the user asks explicitly or the function description allows.
            always follow the functions description.
            functions results are always right and truthful unless the function description says otherwise.
            be honest with the user.
            keep responses helpful and short.
            treat what the user tells you about themselves (name, situation, preferences) as true.
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
        Message message = new Message(Roles.SYSTEM, Objects.requireNonNull(systemPrompt, "systemPrompt must not be null"));
        messageList.add(message);
        sqlite.save(Map.of(sqlite.tableScheme.columns.get(1), Util.gson.toJson(Objects.requireNonNull(message))));
    }

    Conversation(@NotNull String title) {
        this(title, DEFAULT_SYSTEM_PROMPT);
    }

    private void saveMessage(@NotNull List<Message> messages) {
        for (Message m : messages) {
            Objects.requireNonNull(m, "a message in the messages was null");
            saveMessage(m);
        }
    }

    private void saveMessage(@NotNull Message m) {
        if (m.role() == Roles.USER_FACING) return;
        messageList.add(m);
        sqlite.save(Map.of(sqlite.tableScheme.columns.get(1), Util.gson.toJson(Objects.requireNonNull(m))));
    }

    private void saveAiMessage(@NotNull RequestOut request) {
        Objects.requireNonNull(request, "request must not be null");
        Roles role = request.message().role();
        if (role == Roles.USER_FACING) return;
        if (role != Roles.ASSISTANT) {
            throw new IllegalArgumentException("messages in saveAiMessages must be sent by the ai");
        }

        request = requestOutBuilder.appendMessages(request);

        if (request != null) {
            saveMessage(request.message());
        }
    }


    /**
     * this handles the Ais memory per chat and handles queuing too,
     *
     * @param messages a list of the new messages, list in case of multiple tool usage
     * @param ai       the current AI
     */
    public void chat(@NotNull List<Message> messages, @NotNull LocalAi ai, Consumer<RequestOut> todo) {
        Objects.requireNonNull(messages, "messages must not be null");
        Objects.requireNonNull(ai, "ai must not be null");
        LOGGER.debug("received messages {}", messages);
        if (ai.isThinking()) {
            queue.addAll(messages);
            LOGGER.debug("added {} to queue", messages);
            return;
        }
        saveMessage(messages);
        ai.chat(messageList, requestOut -> {
            saveAiMessage(requestOut);
            todo.accept(requestOut);
        });
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