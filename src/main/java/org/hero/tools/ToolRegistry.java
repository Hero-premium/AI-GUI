package org.hero.tools;

import org.hero.Requests;
import org.hero.Requests.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Stream;

/**
 * registers tools via serviceLoader, you can find every tool it found in {@link TOOLS} and get them via the tool's name
 */
public final class ToolRegistry {

    private static final String ROLE_TOOL = "tool";
    private static final Logger LOGGER = LoggerFactory.getLogger(ToolRegistry.class);
    /**
     * an unmodifiable map containing every tool
     */
    public static final Map<String, Tool> TOOLS;

    /**
     * an unmodifiable list containing every tool's data meant to be passed to the AI every request
     *
     * @see Requests.RequestIn
     */
    public static final List<ToolsInformation.ToolData> TOOLS_DATA;

    static {
        Map<String, Tool> map = new HashMap<>();
        Stream<ServiceLoader.Provider<Tool>> providerStream = ServiceLoader.load(Tool.class).stream();

        providerStream.forEach(provider -> {
            try {
                Tool tool = provider.get();
                map.put(tool.toolName, tool);
                LOGGER.info("{} has been added", tool);
            } catch (ServiceConfigurationError e) {
                LOGGER.error("Could not be loaded: {}", e.getMessage(), e);
            }
        });
        TOOLS = Map.copyOf(map);

        List<ToolsInformation.ToolData> toolsData = new ArrayList<>();
        for (Tool tool : TOOLS.values()) {
            toolsData.add(tool.toolsData);
        }
        TOOLS_DATA = List.copyOf(toolsData);
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
     * @param toolData contains data about the tool most importantly its name and parameters
     * @return Optional Requests.Message the message - tool returned, {@code Optional.empty()} if the tool was not found
     */
    public static List<Message> findAndRunTools(List<ToolsInformation.ToolCall> toolData) {
        if (toolData == null) return List.of();
        List<Message> messages = new ArrayList<>();

        for (ToolsInformation.ToolCall toolCall : toolData) {
            ToolsInformation.FunctionCall function = toolCall.function();

            Message message = getTool(function.name())
                    .map(tool -> new Message(ROLE_TOOL, tool.toolUse(function.arguments()), function.name()))
                    .orElseGet(() -> new Message(ROLE_TOOL, "unknown tool", function.name()));
            messages.add(message);
        }
        return List.copyOf(messages);
    }


    private ToolRegistry() {
        throw new AssertionError("no org.hero.tools.ToolRegistry instances for you!");
    }
}