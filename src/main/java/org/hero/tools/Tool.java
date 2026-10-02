package org.hero.tools;

import org.hero.chatai.Requests;

import java.util.*;

/**
 * A tool an AI can use, tools are stateless and unmodifiable so they can be used by the AI as many times with no problem.
 */
public abstract class Tool {

    /**
     * an unmodifiable map containing every tool
     */
    public static final Map<String, Tool> TOOLS;

    static {
        Map<String, Tool> map = new HashMap<>();
        for (Tool tool : ServiceLoader.load(Tool.class)) {
            map.put(tool.getClass().getSimpleName(), tool);
        }
        TOOLS = Map.copyOf(map);
    }

    /**
     * this tells the AI how to use this tool
     */
    public final String howToUse;

    protected Tool(String howToUse) {
        this.howToUse = Objects.requireNonNull(howToUse, "howToUse must not be null");
    }

    /**
     * Search for a tool with the specified name and returns it.
     *
     * @param tool the name of the tool, is the same as its {@code simpleClassName}
     * @return OptionalTool> the tool asked for, Optional.empty() if the tool was not found
     */
    public static Optional<Tool> getTool(String tool) {
        return Optional.ofNullable(TOOLS.get(tool));
    }

    /**
     * Search for a tool with the specified name and runs it then returns its result.
     *
     * @param toolName the name of the tool, is the same as its {@code simpleClassName}
     * @return OptionalRequests.Message> the message the tool returned, Optional.empty() if the tool was not found
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
