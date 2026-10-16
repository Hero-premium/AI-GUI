package org.hero.program.conversations;

import org.hero.Requests;
import org.hero.Roles;
import org.hero.program.sqlite.SQLite;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Conversation {
    private static final SQLite sqlite = new SQLite("""
            CREATE TABLE IF NOT EXISTS conversation (
            id INTEGER PRIMARY KEY ,
            title TEXT ,
            messages TEXT , --jsons
            date DATE
            )
            """);
    private final List<Requests.Message> messages = new ArrayList<>();
    // for deserializing
    private final String title;

    Conversation(String title) {
        this.title = Objects.requireNonNull(title, "title must not be null");
    }

    void saveMessage(@NotNull Requests.Message message) {
        if (message.role() != Roles.SYSTEM) messages.add(Objects.requireNonNull(message, "message must not be null"));
        sqlite.getClass(); // add to database here 😭
    }

    List<Requests.Message> getMessages() {
        return List.copyOf(messages);
    }
}