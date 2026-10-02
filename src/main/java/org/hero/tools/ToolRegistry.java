package org.hero.tools;

import org.hero.chatai.Requests;

import java.util.*;
import java.util.stream.Stream;

/**
 * registers tools via serviceLoader, you can find every tool it found in {@link TOOLS} and get them via the tool's name
 */
public final class ToolRegistry {
    /**
     * an unmodifiable map containing every tool
     */
    public static final Map<String, Tool> TOOLS;

    static {
        Map<String, Tool> map = new HashMap<>();
        Stream<ServiceLoader.Provider<Tool>> providerStream = ServiceLoader.load(Tool.class).stream();

        providerStream.forEach(provider -> {
            try {
                map.put(provider.type().getSimpleName(), provider.get());
            } catch (ServiceConfigurationError e) {
                System.err.println("Skipping broken tool: " + e.getMessage());
            }
        });
        TOOLS = Map.copyOf(map);
    }


    /**
     * Search for a tool with the specified name and returns it.
     *
     * @param tool the name of the tool, is the same as its {@code simpleClassName}
     * @return Optional the tool asked for, {@code Optional.empty()} if the tool was not found
     */
    public static Optional<Tool> getTool(String tool) {
        return Optional.ofNullable(TOOLS.get(tool));
    }

    /**
     * Search for a tool with the specified name and runs it then returns its result.
     *
     * @param toolName the name of the tool, is the same as its {@code simpleClassName}
     * @return Optional Requests.Message the message - tool returned, {@code Optional.empty()} if the tool was not found
     */
    public static Optional<Requests.Message> findAndRunTool(String toolName) {
        return getTool(toolName).map(tool -> new Requests.Message("tool", tool.useTool()));
    }

    private ToolRegistry() {
        throw new AssertionError("no org.hero.tools.ToolRegistry instances for you!");
    }
}