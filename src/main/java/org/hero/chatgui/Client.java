package org.hero.chatgui;


import org.hero.program.Program;

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
    public abstract void displayUserMessage(String message);

    /**
     * this is to display the tools separately, typically above normal messages in a small box
     *
     * @param message the name and the return of the tool
     */
    public abstract void displayToolsMessage(String message);

    /**
     * this is to display a message on the screen that was sent by the Ai/assistant
     *
     * @param message the thing to display
     */
    public abstract void displayAIMessage(String message);

    /**
     * this is to display a message on the screen that was sent by the system
     *
     * @param message the thing to display
     */
    public abstract void displaySystemMessage(String message);


    public Client setProgram(Program program) {
        this.program = Objects.requireNonNull(program, "program must not be null");
        return this;
    }
}