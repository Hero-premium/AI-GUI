package org.hero.chatgui.javafx;

import javafx.scene.control.Label;

import java.util.Objects;

class GuiMessage {
    private final String message;

    GuiMessage(String message) {
        this.message = Objects.requireNonNull(message, "message must not be null");
    }

    Label displayMessage() {
        Label label = new Label(message);
        label.setWrapText(true);
        return label;
    }
}
