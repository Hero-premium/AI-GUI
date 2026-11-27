package io.github.hero_premium.clients.web;

import io.github.hero_premium.program.Program;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class Controller {
    private static final Logger LOGGER = LoggerFactory.getLogger(Controller.class);
    private final Program program;

    public Controller(Program program) {
        this.program = Objects.requireNonNull(program, "program must not be null");
    }

    @GetMapping("/chat")
    public void chat(String toSay) {
        LOGGER.debug("received {} from server", toSay);
        program.chat(toSay);
    }
}
