package io.github.hero_premium.clients;


import io.github.hero_premium.program.Program;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class Client {

    protected Program program;

    /**
     * prepares and runs the necessary methods for the application to start
     */
    public abstract void launchApplication();

    /**
     * this is to display a message on the screen that was sent by the user
     *
     * @param message the thing to display
     */
    public abstract void displayUserMessage(@NotNull String message);

    /**
     * this is to display the tools separately, typically above normal messages in a small box
     *
     * @param message the name and the return of the tool
     */
    public abstract void displayToolsMessage(@NotNull String message);

    /**
     * this is to display a message on the screen that was sent by the Ai/assistant
     *
     * @param message the thing to display
     */
    public abstract void displayAIMessage(@NotNull String message);

    /**
     * this is to display a message on the screen that was sent by the system
     *
     * @param message the thing to display
     */
    public abstract void displaySystemMessage(@NotNull String message);

    /**
     * used alongside streaming mode to indicate the end of a message,
     * for example you keep appending with {@link IO#print(Object)} then at the end of message call {@link IO#println(Object)}
     * for CLI based applications
     */
    public void endLine() {
        endLine("// your turn");
    }

    /**
     * used alongside streaming mode to indicate the end of a message,
     * for example you keep appending with {@link IO#print(Object)} then at the end of message call {@link IO#println(Object)}
     * for CLI based applications
     *
     * @param message a custom message to display
     */
    public abstract void endLine(String message);

    /**
     *
     * @param message the chunk to display in the thinking box.
     */
    public abstract void displayThinking(String message);

    /**
     * ends the line of thinking so new thinking calls appears on a separate line.
     */
    public abstract void endLineOfThinking();


    public Client setProgram(@NotNull Program program) {
        this.program = Objects.requireNonNull(program, "program must not be null");
        return this;
    }

    public Client getClient() {
        return this;
    }
}