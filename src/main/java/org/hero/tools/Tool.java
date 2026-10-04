package org.hero.tools;

import java.util.Map;

/**
 * A tool an AI can use, tools are stateless and unmodifiable so they can be used by the AI as many times with no problem.
 */
public abstract class Tool {

    protected record Param(String argumentName, ToolsInformation.PropertiesType type, String description,
                           boolean isRequired) {
    }

    public final ToolsInformation.ToolData toolsData;
    public final String toolName;

    protected Tool(String name, String howToUse, Param... params) {
        var builder = ToolsInformation.ToolDataBuilder.builder();
        for (Param p : params) {
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
}
