package org.hero.tools;

import org.hero.chatai.Requests;

import java.util.*;

public abstract class Tool {

    private static final Map<String, Tool> tools = new HashMap<>();

    static {
        for (Tool tool : ServiceLoader.load(Tool.class)) {
            tools.put(tool.getClass().getSimpleName(), tool);
        }
    }

    /**
     * this tells the AI how to use this tool
     */
    public final String howToUse;

    protected Tool(String howToUse) {
        this.howToUse = Objects.requireNonNull(howToUse, "howToUse must not be null");
    }

    /**
     *
     * @return A copy of the Map that contains every tool we have
     */
    public static Map<String, ? extends Tool> getTools() {
        return new HashMap<>(tools);
    }

    /**
     * Search for a tool with the specified name and returns it.
     *
     * @param tool the name of the tool, is the same as its {@code simpleClassName}
     * @return Optional<Tool> the tool asked for, null if the tool was not found
     */
    public static Optional<Tool> getTool(String tool) {
        return Optional.ofNullable(tools.get(tool));
    }

    /**
     * Search for a tool with the specified name and runs it then returns its result.
     *
     * @param toolName the name of the tool, is the same as its {@code simpleClassName}
     * @return Optional<Requests.Message> the message the tool returned, null if the tool was not found
     */
    public static Optional<Requests.Message> detectAndRunTool(String toolName) {
        return getTool(toolName).map(tool -> new Requests.Message("tool", tool.useTool()));
    }

    /**
     * this runs whenever a tool usage is detected
     *
     * @return the tool's result
     */
    protected abstract String useTool();


}
