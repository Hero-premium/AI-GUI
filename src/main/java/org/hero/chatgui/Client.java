package org.hero.chatgui;

public interface Client {


    /**
     * prepares and runs the nessaray methods for the application to start
     */
    void launchApplication();

    /**
     * this is to display a message on the screen either by the ai or user
     * @param message the thing to display
     */
    void displayMessage(String message);

    /**
     * this is to display the tools separately, typically above normal messages in a small box
     * @param tool the name and the return of the tool
     */
    void toolsDisplay(String tool);
}