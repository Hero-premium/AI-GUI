package io.github.hero_premium.program.conversations;

import io.github.hero_premium.program.sqlite.Constants;
import io.github.hero_premium.program.sqlite.CorruptDataException;
import io.github.hero_premium.program.sqlite.SQLiteTable;
import io.github.hero_premium.program.sqlite.schemes.TableScheme;
import io.github.hero_premium.program.sqlite.schemes.TableScheme.Column;
import io.github.hero_premium.program.sqlite.schemes.TableScheme.ColumnType;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.*;

public class ConversationsManager {

    public static final String CONVERSATION_PREFIX = "conversation_";
    private final SQLiteTable sqliteTable;

    private final Map<UUID, Conversation> conversations;
    @NotNull
    private volatile Conversation currentConversation;

    public ConversationsManager() {
        this(Constants.DEFAULT_URL);
    }

    public ConversationsManager(String dataBaseName) {
        conversations = new HashMap<>();
        sqliteTable = new SQLiteTable(TableScheme.tableBuilder()
                .name("conversations")
                .column("uuid", ColumnType.TEXT)
                .column("createdAt", ColumnType.TEXT)
                .column("lastModifiedAt", ColumnType.TEXT)
                .column("title", ColumnType.TEXT)
                .build(), dataBaseName
        );
        List<Column> columns = sqliteTable.tableScheme.columns;
        List<String> uuids = sqliteTable.loadColumn(columns.getFirst(), String.class);
        List<String> createdAts = sqliteTable.loadColumn(columns.get(1), String.class);
        List<String> lastModifiedAts = sqliteTable.loadColumn(columns.get(2), String.class);
        List<String> titles = sqliteTable.loadColumn(columns.get(3), String.class);

        for (int i = 0; i < uuids.size(); i++) {
            String uuid = getUuid(uuids.get(i));
            String title = titles.get(i);
            String createdAt = createdAts.get(i);
            String lastModifiedAt = lastModifiedAts.get(i);
            Conversation convo = new Conversation(title == null ? "untitled" : title, Conversation.DEFAULT_SYSTEM_PROMPT, UUID.fromString(uuid));
            conversations.put(convo.uuid, convo);
        }
        currentConversation = newConversation("untitled", dataBaseName);
    }

    private @NonNull String getUuid(@NonNull String uuid) {
        if (!uuid.startsWith(CONVERSATION_PREFIX))
            throw new CorruptDataException("uuid %s doesn't start with '%s' somehow".formatted(uuid, CONVERSATION_PREFIX));
        return uuid.substring(CONVERSATION_PREFIX.length());
    }

    /**
     * @return the current conversation
     */
    public @NotNull Conversation getCurrentConversation() {
        return currentConversation;
    }

    /**
     * starts a new conversation and returns it
     *
     * @param title the title of this conversation
     * @return the new conversation created
     */
    public @NotNull Conversation newConversation(String title, String dataBaseName) {
        currentConversation = new Conversation(title, Conversation.DEFAULT_SYSTEM_PROMPT, UUID.randomUUID(), dataBaseName);
        conversations.put(currentConversation.uuid, currentConversation);
        sqliteTable.save(Map.of(sqliteTable.tableScheme.columns.getFirst(), "conversation_" + currentConversation.uuid));
        return currentConversation;
    }

    public @NotNull Conversation newConversation(String title) {
        return newConversation(title, Constants.DEFAULT_URL);
    }

    /**
     *
     * @param uuid the uuid of the new convo
     * @throws NoSuchElementException if this uuid does not exist.
     */
    public void setCurrentConversation(UUID uuid) {
        if (!conversations.containsKey(uuid)) throw new NoSuchElementException("the uuid " + uuid + " does not exist");
        setCurrentConversation(conversations.get(uuid));
    }

    /**
     * sets current conversation to the conversation provided
     *
     * @param conversation the new current conversation
     * @throws NullPointerException on null input
     */
    public void setCurrentConversation(@NotNull Conversation conversation) {
        this.currentConversation = Objects.requireNonNull(conversation, "conversation must not be null");
    }

    public Map<UUID, Conversation> getConversations() {
        return Map.copyOf(conversations);
    }

}
