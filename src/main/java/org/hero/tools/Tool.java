package org.hero.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * A tool an AI can use, tools are stateless and unmodifiable so they can be used by the AI as many times with no problem.
 */
public abstract class Tool {

    protected record Param(String argumentName, PropertiesType type, String description,
                           boolean isRequired) {
    }

    private final Logger LOGGER = LoggerFactory.getLogger(Tool.class);
    public final ToolsInformation.ToolData toolsData;
    public final String toolName;
    protected final Param[] params;

    protected Tool(String name, String howToUse, Param... params) {
        this.params = Objects.requireNonNull(params, "params must not be null");
        var builder = ToolsInformation.ToolDataBuilder.builder();
        for (Param p : params) {
            Objects.requireNonNull(p, "some parameter is null");
            builder.parameter(p.argumentName(), p.type(), p.description(), p.isRequired());
        }
        this.toolsData = builder.buildParameters()
                .function(name, howToUse)
                .build();
        this.toolName = name;
    }

    /**
     * this runs whenever a tool usage is detected
     *
     * @return the tool's result
     */
    protected abstract String useTool(Map<String, Object> arguments);

    @Override
    public String toString() {
        return "tool " + getClass().getSimpleName();
    }

    @SuppressWarnings("LoggingSimilarMessage")
    String toolUse(Map<String, Object> tools) {
        // if it takes no params don't validate anything
        // map.of to stop nulls
        if (params.length == 0) return useTool(Map.of());

        if (tools == null) {
            LOGGER.warn("ai misused the tool, {}", "TOOL ERROR, NULL BEEN PASSED");
            return "TOOL ERROR, NULL BEEN PASSED";
        }

        if (isRequiredParamMissing(tools)) {
            LOGGER.warn("ai misused the tool, {}", "TOOL ERROR, MISSING A REQUIRED PARAMETER");
            return "TOOL ERROR, MISSING A REQUIRED PARAMETER";
        }

        for (Param p : params) {
            String errorMessage = validateArguments(tools, p);
            if (errorMessage != null) return errorMessage;
        }

        return useTool(tools);
    }

    private String validateArguments(Map<String, Object> tools, Param p) {
        Object object = tools.get(p.argumentName());


        if ((object != null || p.isRequired()) && !p.type().getType().isInstance(object)) {
            String typeToString = "UNKNOWN";
            for (PropertiesType propertiesType : PropertiesType.values()) {
                if (propertiesType.getType().isInstance(object)) {
                    typeToString = propertiesType.toString();
                }
            }
            String errorMessage = "TOOL ERROR, EXPECTED " + p.type() + ", RECEIVED " + typeToString;
            LOGGER.warn("ai misused the tool, {}", errorMessage);
            return errorMessage;
        }
        return null;
    }

    private boolean isRequiredParamMissing(Map<String, Object> arguments) {
        return Stream.of(params)
                .anyMatch(p -> p.isRequired() && !arguments.containsKey(p.argumentName()));
    }
}
