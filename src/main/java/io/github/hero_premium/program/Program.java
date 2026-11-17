package io.github.hero_premium.program;

import io.github.hero_premium.Requests.Message;
import io.github.hero_premium.Requests.RequestOut;
import io.github.hero_premium.Roles;
import io.github.hero_premium.clients.Client;
import io.github.hero_premium.commands.CommandsRegistry;
import io.github.hero_premium.localais.Llama3b;
import io.github.hero_premium.localais.LocalAi;
import io.github.hero_premium.localais.LocalAiRegistry;
import io.github.hero_premium.program.conversations.Conversation;
import io.github.hero_premium.program.conversations.ConversationsManager;
import io.github.hero_premium.tools.ToolRegistry;
import io.github.hero_premium.tools.ToolsInformation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Program {

    private static final int MAX_TOOLS_REQUESTS = 5;
    private static final Logger LOGGER = LoggerFactory.getLogger(Program.class);
    @NotNull
    private final ConversationsManager conversationsManager = new ConversationsManager();
    @NotNull
    private final Client client;
    @NotNull
    private LocalAi ai;

    public void setAi(LocalAi ai) {
        this.ai = Objects.requireNonNull(ai, "ai must not be null");
    }

    public Program(@NotNull Client client, LocalAi ai) {
        Objects.requireNonNull(client, "client must not be null");
        Client varClient = Objects.requireNonNull(client.getClient(), "client.getClient() must not return null");
        this.client = Objects.requireNonNull(varClient.setProgram(this), "client.setProgram(Program) must not return null");
        this.ai = Objects.requireNonNull(ai, "ai must not be null");
    }

    public Program(@NotNull Client client) {
        this(client, new Llama3b());
    }

    public void launch() {
        client.launchApplication();
    }

    /**
     * call this to chat with the AI, it handles tools and commands and queue
     *
     * @param message the message of what to tell the AI
     */
    public void chat(@NotNull Message message) {
        String content = message.content();
        if (content == null || content.isBlank()) return;
        displayMessage(message);
        Conversation conversation = conversationsManager.getCurrentConversation();
        if (handleCommands(content, conversation)) return;

        conversation.chat(message, ai, requestOut -> chatHandler(requestOut, conversation));

        // FIXME a very very long loop when there's a queue
        // if (conversation.isQueued()) chat(conversation.getNext());
    }

    public void chat(String request) {
        chat(new Message(Roles.USER, request));
    }

    private void chatHandler(@NotNull RequestOut requestOut, Conversation conversation) {
        requestOut = handleTools(requestOut, conversation);
        if (requestOut == null) return;
        displayMessage(requestOut.message());
        if (requestOut.done()) client.endLine();
    }

    private final List<Message> toolReplies = new CopyOnWriteArrayList<>();
    private final AtomicInteger toolLoops = new AtomicInteger();

    private @Nullable RequestOut handleTools(@NotNull RequestOut requestOut, final Conversation conversation) {
        Objects.requireNonNull(requestOut, "requestOut must not be null");

        // todo bring back the max limit + fix bugs
        List<ToolsInformation.ToolCall> toolCalls = requestOut.message().tool_calls();
        if (toolCalls != null && !toolCalls.isEmpty()) {
            toolLoops.incrementAndGet();
            if (toolLoops.get() >= MAX_TOOLS_REQUESTS) {
                toolLoops.set(0);
                return new RequestOut(
                        requestOut.model(),
                        requestOut.created_at(),
                        new Message(Roles.USER_FACING, "the AI hit a limit and couldn't generate a response, please try again"),
                        true,
                        "failure",
                        requestOut.total_duration(),
                        requestOut.load_duration(),
                        requestOut.prompt_eval_count(),
                        requestOut.prompt_eval_cached_count(),
                        requestOut.prompt_eval_duration(),
                        requestOut.eval_count(),
                        requestOut.eval_duration(),
                        requestOut.error());
            }
        }
        toolReplies.addAll(ToolRegistry.findAndRunTools(toolCalls));

        if (requestOut.done() && !toolReplies.isEmpty()) {
            List<Message> copy = List.copyOf(toolReplies);
            toolReplies.clear();
            displayAsTool(copy);
            conversation.chat(copy, ai, requestOut1 -> chatHandler(requestOut1, conversation));
            return null;
        }

        String content = requestOut.message().content();
        if (requestOut.done() || content != null && !content.isEmpty()) {
            toolLoops.set(0);
            return requestOut;
        }
        return null;
    }

    private void displayAsTool(@NotNull Collection<Message> toolReplies) {
        toolReplies.stream()
                .map(toolCalls -> toolCalls.tool_name() + " " + toolCalls.content())
                .forEach(client::displayToolsMessage);
    }

    private void displayMessage(@NotNull Message message) {
        String content = message.content();
        if (content == null) return;
        switch (message.role()) {
            case USER -> client.displayUserMessage(content);
            case TOOL -> client.displayToolsMessage(content);
            case ASSISTANT -> client.displayAIMessage(content);
            case SYSTEM, USER_FACING -> client.displaySystemMessage(content);
            default -> LOGGER.warn("switch in io.github.hero_premium.program.Program reached an unexpected case");
        }
    }

    private boolean handleCommands(@NotNull String request, Conversation conversation) {
        if (!request.startsWith("/")) return false;
        for (Message message : CommandsRegistry.findAndRunCommand(request, conversation, client)) {
            displayMessage(message);
        }
        return true;
    }

    public Map<String, LocalAi> getAllAis() {
        return LocalAiRegistry.AIS;
    }

}