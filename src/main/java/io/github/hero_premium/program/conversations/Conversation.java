package io.github.hero_premium.program.conversations;

import com.google.gson.JsonSyntaxException;
import io.github.hero_premium.Requests.Message;
import io.github.hero_premium.Requests.RequestOut;
import io.github.hero_premium.Role;
import io.github.hero_premium.localais.LocalAi;
import io.github.hero_premium.program.sqlite.Constants;
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

    public static final String DEFAULT_SYSTEM_PROMPT = """
            You are the assistant. Follow these rules when talking to the user.
            Only if the user asks where you are running: say you are running on a local device (offline, on the user's device).
            If you don't know something or aren't sure, say so.
            Reply in the same language the user is using.
            Do not use functions unless the user explicitly asks or the function description allows it.
            Always follow the functions' descriptions.
            Function results are always right and truthful unless the function description says otherwise.
            Be honest with the user.
            Keep responses helpful and short.
            Treat what the user tells you about themselves (name, situation, preferences) as true.
            Do not help with harmful requests.
            """;

    private static final Logger LOGGER = LoggerFactory.getLogger(Conversation.class);

    private final RequestOutBuilder requestOutBuilder = new RequestOutBuilder();
    private final Deque<Message> queue = new ArrayDeque<>();

    @NotNull
    private final List<Message> messageList;
    @NotNull
    public final UUID uuid;
    @NotNull
    private String title;
    @NotNull
    private final SQLiteTable sqlite;


    Conversation(@NotNull String title) {
        this(title, DEFAULT_SYSTEM_PROMPT);
    }

    Conversation(String title, @NotNull UUID uuid) {
        this(title, uuid, Constants.DEFAULT_URL);
    }

    Conversation(String title, UUID uuid, String url) {
        this(title, DEFAULT_SYSTEM_PROMPT, uuid, url);
    }

    Conversation(@NotNull String title, @NotNull String systemPrompt) {
        this(title, systemPrompt, UUID.randomUUID());
    }

    Conversation(@NotNull String title, @NotNull String systemPrompt, @NotNull UUID uuid) {
        this(title, systemPrompt, uuid, Constants.DEFAULT_URL);
    }

    Conversation(@NotNull String title, @NotNull String systemPrompt, @NotNull UUID uuid, @NotNull String sqlUrl) {
        this.uuid = Objects.requireNonNull(uuid, "uuid must not be null");
        this.title = Objects.requireNonNull(title, "title must not be null");
        this.sqlite = new SQLiteTable(TableScheme.tableBuilder()
                .name("conversation_" + this.uuid)
                .column("id", TableScheme.ColumnType.INTEGER)
                .column("messages", TableScheme.ColumnType.TEXT)
                .build()
                , sqlUrl);
        this.messageList = new ArrayList<>();
        List<String> jsons = sqlite.loadColumn(sqlite.tableScheme.columns.get(1), String.class);
        List<Message> messages = jsons.stream()
                .map(json -> {
                    try {
                        return Util.gson.fromJson(json, Message.class);
                    } catch (JsonSyntaxException _) {
                        LOGGER.error("failed to retrieve message: {}", json);
                        //todo add better handling
                        return new Message(Role.ASSISTANT, "something went wrong while retrieve this message");
                    }
                })
                .toList();
        messageList.addAll(messages);
        if (messageList.isEmpty()) {
            saveMessage(new Message(Role.SYSTEM, Objects.requireNonNull(systemPrompt, "systemPrompt must not be null")));
        }
    }

    private void saveMessage(@NotNull Message message) {
        Objects.requireNonNull(message, "message must not be null");
        if (message.role() == Role.USER_FACING) return;
        messageList.add(message);
        sqlite.save(Map.of(sqlite.tableScheme.columns.get(1), Util.gson.toJson(Objects.requireNonNull(message))));
    }

    private void saveAiMessage(@NotNull RequestOut request) {
        Objects.requireNonNull(request, "request must not be null");
        Role role = request.message().role();
        if (role == Role.USER_FACING) return;
        if (role != Role.ASSISTANT) {
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
        Objects.requireNonNull(todo, "todo must not be null");

        LOGGER.debug("received messages {}", messages);
        if (ai.isBusy()) {
            queue.addAll(messages);
            LOGGER.debug("added {} to queue", messages);
            return;
        }
        messages.forEach(this::saveMessage);
        ai.chat(messageList, requestOut -> {
            saveAiMessage(requestOut);
            todo.accept(requestOut);
        });
    }

    public void chat(Message message, LocalAi ai, Consumer<RequestOut> todo) {
        chat(List.of(message), ai, todo);
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

    void setTitle(@NotNull String title) {
        this.title = Objects.requireNonNull(title, "title must not be null");
    }

    public @NotNull String getTitle() {
        return title;
    }
}