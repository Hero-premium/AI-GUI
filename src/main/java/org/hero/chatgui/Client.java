package org.hero.chatgui;

public interface Client {


    /**
     * prepares and runs the necessary methods for the application to start
     */
    void launchApplication();

    /**
     * this is to display a message on the screen that was sent by the user
     *
     * @param message the thing to display
     */
    void displayUserMessage(String message);

    /**
     * this is to display the tools separately, typically above normal messages in a small box
     *
     * @param message the name and the return of the tool
     */
    void displayToolsMessage(String message);

    /**
     * this is to display a message on the screen that was sent by the Ai/assistant
     *
     * @param message the thing to display
     */
    void displayAIMessage(String message);

    /**
     * this is to display a message on the screen that was sent by the system
     *
     * @param message the thing to display
     */
    void displaySystemMessage(String message);
}