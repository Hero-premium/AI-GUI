package io.github.hero_premium.program;

import io.github.hero_premium.Requests;
import io.github.hero_premium.clients.Client;

import java.util.concurrent.atomic.AtomicBoolean;

class ThinkingDisplayHandler {

    private final AtomicBoolean displayed = new AtomicBoolean(false);

    void display(Requests.RequestOut requestOut, Client client) {
        String thinking = requestOut.message().thinking();
        if (thinking != null) {
            client.displayThinking(thinking);
        } else if (!requestOut.done() && displayed.compareAndSet(false, true)) {
            client.endLineOfThinking();
        } else if (requestOut.done()) {
            displayed.set(false);
        }
    }
}
