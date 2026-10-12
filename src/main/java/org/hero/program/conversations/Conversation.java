package org.hero.program.conversations;

import org.hero.Requests;
import org.hero.program.sqlite.SQLite;

import java.util.ArrayList;
import java.util.List;

class Conversation {
    private final SQLite sqlite = new SQLite("""
                CREATE TABLE IF NOT EXISTS conversations (
                id INTEGER PRIMARY KEY ,
                title TEXT ,
                messages TEXT , --jsons
                date INTEGER
                )
                """);
    private final List<Requests.Message> messages = new ArrayList<>();

}
