package io.github.hero_premium.chatai;

import io.github.hero_premium.Requests;
import io.github.hero_premium.Roles;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RequestsTest {
    @Test
    void getEvalDurationInSeconds() {
        int result = new Requests.RequestOut("system", "",
                new Requests.Message(Roles.SYSTEM, "ollama server is down"),
                true, "", 0, 0,
                0, 0, 0,
                0, 5_000_000_000L).getEvalDurationInSeconds();
        assertEquals(5, result);
    }
}