package io.github.hero_premium.program;

import io.github.hero_premium.Requests;
import io.github.hero_premium.clients.Client;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;

class ThinkingDisplayHandler {

    private final AtomicBoolean displayed = new AtomicBoolean(false);
    private final AtomicBoolean thoughtOnce = new AtomicBoolean(false);

    void display(Requests.@NotNull RequestOut requestOut, @NotNull Client client) {
        String thinking = requestOut.message().thinking();
        if (thinking != null) {
            client.displayThinking(thinking);
            thoughtOnce.compareAndSet(false, true);
        } else if (!requestOut.done() &&
                displayed.compareAndSet(false, true) &&
                thoughtOnce.get()) {
            client.endLineOfThinking();
        } else if (requestOut.done()) {
            displayed.set(false);
            thoughtOnce.set(false);
        }
    }
}
