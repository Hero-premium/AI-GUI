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

    protected Tool(String howToUse) {
        this.howToUse = Objects.requireNonNull(howToUse, "howToUse must not be null");
    }

    /**
     * this runs whenever a tool usage is detected
     *
     * @return the tool's result
     */
    protected abstract String useTool();


}
