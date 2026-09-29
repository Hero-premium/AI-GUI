package org.hero.tools;

import org.hero.chatai.Requests;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;

public abstract class Tool {


    private static final Map<String, Tool> tools = new HashMap<>();

    static {
        for (Tool tool : ServiceLoader.load(Tool.class)) {
            tools.put(tool.getClass().getSimpleName(), tool);
        }
    }

    public String howToUse;

    protected Tool() {
    }

    public static Map<String, ? extends Tool> getTools() {
        return new HashMap<>(tools);
    }

    public static Optional<Tool> getTool(String tool) {
        return Optional.ofNullable(tools.get(tool));
    }

    public static Optional<Requests.Message> detectAndRunTool(String toolName) {
        return getTool(toolName).map(value -> new Requests.Message("tool", value.useTool()));
    }

    protected abstract String useTool();


}
