package io.github.hero_premium.program.conversations;

import io.github.hero_premium.program.sqlite.SQLite;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConversationsManager {

    private static final SQLite sqLite = new SQLite("""
            CREATE TABLE IF NOT EXISTS conversations(
            id  INTEGER PRIMARY KEY AUTOINCREMENT,
            conversation TEXT
            )
            """);
    private static final List<Conversation> conversations = new ArrayList<>();
    @NotNull
    private Conversation currentConversation = new Conversation("untitled");

    /**
     * @return the current conversation
     * @throws IllegalStateException if the current conversation is null
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
        return currentConversation;
    }

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

    public int lastConversationIndex() {
        return conversations.size() - 1;
    }
}
