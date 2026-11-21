package io.github.hero_premium.program.conversations;

import io.github.hero_premium.program.sqlite.SQLiteTable;
import io.github.hero_premium.program.sqlite.schemes.TableScheme;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class ConversationsManager {

    private static final SQLiteTable sqliteTable = new SQLiteTable(TableScheme.tableBuilder()
            .name("conversations")
            .column("id", TableScheme.ColumnType.INTEGER)
            .column("conversation", TableScheme.ColumnType.TEXT)
            .build()
    );

    private final Map<UUID, Conversation> conversations = new HashMap<>();
    @NotNull
    private Conversation currentConversation = new Conversation("untitled");

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
        conversations.put(currentConversation.uuid, currentConversation);
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
