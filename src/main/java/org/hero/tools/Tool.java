package org.hero.tools;

import java.util.Objects;

/**
 * A tool an AI can use, tools are stateless and unmodifiable so they can be used by the AI as many times with no problem.
 */
public abstract class Tool {


    /**
     * this tells the AI how to use this tool
     */
    public final String howToUse;

    public final ToolStuff.ToolData toolsData;

    protected Tool(String howToUse, String name) {
        this.howToUse = Objects.requireNonNull(howToUse, "howToUse must not be null");
        this.toolsData = ToolStuff.ToolDataBuilder
                .startToolDataBuilder()
                .parameter("query", ToolStuff.PropertiesType.STRING, "use this as the search query", true)
                .buildParameters()
                .function(name, howToUse)
                .build();
    }

    /**
     * this runs whenever a tool usage is detected
     *
     * @return the tool's result
     */
    protected abstract String useTool();


}
