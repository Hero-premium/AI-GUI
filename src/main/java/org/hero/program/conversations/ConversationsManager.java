package org.hero.program.conversations;

import org.hero.program.sqlite.SQLite;

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
    private Conversation currentConversation;

    public Conversation getCurrentConversation() {
        if (Objects.isNull(currentConversation)) {
            throw new IllegalStateException("Current conversation is null");
        }
        return currentConversation;
    }

    public Conversation newConversation(String title) {
        currentConversation = new Conversation(title);
        conversations.add(currentConversation);
        return currentConversation;
    }

    public void setCurrentConversation(int index) {
        currentConversation = conversations.get(index);
    }
}
