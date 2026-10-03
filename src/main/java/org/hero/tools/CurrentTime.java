package org.hero.tools;

import java.time.LocalDate;
import java.util.Map;

public class CurrentTime extends Tool{


    public CurrentTime() {
        super("CurrentTime", "call this when you need to know about the current date and time, y");
    }

    @Override
    protected String useTool(Map<String, Object> tools) {
        return LocalDate.now().toString();
    }
}
