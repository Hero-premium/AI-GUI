package org.hero.tools;

import java.time.LocalDateTime;
import java.util.Map;

public class CurrentTime extends Tool {


    public CurrentTime() {
        super("CurrentTime", "call this when you need to know about the current date and time");
    }

    @Override
    protected String useTool(Map<String, Object> tools) {
        return LocalDateTime.now().toString();
    }
}
