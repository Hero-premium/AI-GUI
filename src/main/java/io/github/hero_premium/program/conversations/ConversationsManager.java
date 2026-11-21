package io.github.hero_premium.program.conversations;

import io.github.hero_premium.program.sqlite.SQLiteTable;
import io.github.hero_premium.program.sqlite.schemes.TableScheme;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ConversationsManager {

    private static final SQLiteTable sqliteTable = new SQLiteTable(TableScheme.tableBuilder()
            .name("conversations")
            .column("uuid", TableScheme.ColumnType.TEXT)
            .column("createdAt", TableScheme.ColumnType.TEXT)
            .column("lastModifiedAt", TableScheme.ColumnType.TEXT)
            .column("title", TableScheme.ColumnType.TEXT)
            .build()
    );

    private final List<Conversation> conversations;
    @NotNull
    private Conversation currentConversation = new Conversation("untitled");

    public ConversationsManager() {
        conversations = new ArrayList<>();
        List<String> uuids = sqliteTable.loadColumn(sqliteTable.tableScheme.columns.getFirst(), String.class);
        List<String> createdAts = sqliteTable.loadColumn(sqliteTable.tableScheme.columns.get(1), String.class);
        List<String> lastModifiedAts = sqliteTable.loadColumn(sqliteTable.tableScheme.columns.get(2), String.class);
        List<String> titles = sqliteTable.loadColumn(sqliteTable.tableScheme.columns.get(3), String.class);

        for (int i = 0; i < uuids.size(); i++) {
            String uuid = uuids.get(i);
            String title = titles.get(i);
            String createdAt = createdAts.get(i);
            String lastModifiedAt = lastModifiedAts.get(i);

            conversations.add(new Conversation(title,
                    Conversation.DEFAULT_SYSTEM_PROMPT,
                    UUID.fromString(uuid),
                    null)
            );
        }
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
    public Conversation newConversation(String title) {
        currentConversation = new Conversation(title);
        conversations.add(currentConversation);
        sqliteTable.save(Map.of(sqliteTable.tableScheme.columns.get(1), currentConversation.uuid));
        return currentConversation;
    }

    //FIXME int is not a key, it's a UUID
    public void setCurrentConversation(int index) {
        setCurrentConversation(conversations.get(index));
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

}
