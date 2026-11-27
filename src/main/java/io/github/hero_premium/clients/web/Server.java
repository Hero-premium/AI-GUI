package io.github.hero_premium.clients.web;

import io.github.hero_premium.clients.Client;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SuppressWarnings("unused")
@SpringBootApplication
public class Server extends Client {

    @Override
    public void launchApplication() {
        SpringApplication springApplication = new SpringApplication(Server.class);
        springApplication.addInitializers(context ->
                context.getBeanFactory().registerSingleton("program", program));
        springApplication.run();
    }

    @Override
    public void displayUserMessage(@NotNull String message) {

    }

    @Override
    public void displayToolsMessage(@NotNull String message) {

    }

    @Override
    public void displayAIMessage(@NotNull String message) {

    }

    @Override
    public void displaySystemMessage(@NotNull String message) {

    }

    @Override
    public void endLine(String message) {

    }

    @Override
    public void displayThinking(String message) {

    }

    @Override
    public void endLineOfThinking() {

    }
}
