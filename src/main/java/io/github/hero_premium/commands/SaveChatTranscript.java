package io.github.hero_premium.commands;

import io.github.hero_premium.Requests;
import io.github.hero_premium.Roles;
import io.github.hero_premium.chatgui.Client;
import io.github.hero_premium.program.conversations.Conversation;
import io.github.hero_premium.util.Util;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class SaveChatTranscript extends Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(SaveChatTranscript.class);

    public SaveChatTranscript() {
        super("/saveChatTranscript");
    }

    @Override
    protected List<Requests.Message> runCommand(@NotNull Conversation conversation, Client client, String[] arguments) {
        String result;
        try (FileWriter fileWriter = new FileWriter("transcript.JSON")) {
            String json = Util.gson.toJson(conversation.getMessages());
            fileWriter.write(json);
            result = "saved transcript successfully";
        } catch (IOException e) {
            result = "failed to create transcript";
            LOGGER.error(result, e);
        }
        return List.of(new Requests.Message(Roles.SYSTEM, result));
    }
}
