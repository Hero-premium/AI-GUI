package io.github.hero_premium.clients.javafx;

import io.github.hero_premium.clients.Client;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

public class JavaFX extends Client {

    private static final Logger LOGGER = LoggerFactory.getLogger(JavaFX.class);

    private final VBox messages = new VBox(5);
    private ScrollPane scrollPane;

    public JavaFX(@NotNull Stage primaryStage) {
        scrollPane = new ScrollPane(messages);
        messages.setPadding(new Insets(10));
        scrollPane.setFitToWidth(true);
        messages.heightProperty().addListener((_, _, _) -> scrollPane.setVvalue(1.0));

        BorderPane root = new BorderPane();
        root.setCenter(scrollPane);
        root.setBottom(generateTextField());

        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.setTitle("AI GUI");
        primaryStage.show();
    }

    public JavaFX() {
        // NO OP so JavaFXLauncher can be call the one with stage
    }

    private void addChild(String message, Pos pos) {
        HBox row = new HBox(new GuiMessage(message).getLabel());
        row.setAlignment(pos);
        Platform.runLater(() -> messages.getChildren().add(row));
    }

    @Override
    public void launchApplication() {
    }

    @Override
    public void displayUserMessage(String message) {
        addChild(message, Pos.CENTER_LEFT);
    }

    @Override
    public void displayToolsMessage(String message) {
        addChild("TOOLS [ " + message + " ]", Pos.CENTER_RIGHT);
    }


    @Override
    public void displayAIMessage(String message) {
        addChild(message, Pos.CENTER_RIGHT);
    }

    @Override
    public void displaySystemMessage(@NotNull String message) {
        addChild("IMPORTANT: " + message.toUpperCase(Locale.ROOT), Pos.CENTER);
    }

    private TextField generateTextField() {
        TextField textField = new TextField();
        textField.setOnAction(_ -> {
            sendAndDisplayText(textField.getText());
            textField.clear();
        });
        textField.setPromptText("Type a message...");
        return textField;
    }

    private void sendAndDisplayText(String message) {
        Task<Void> task = new Task<>() {
            @Override
            protected @Nullable Void call() {
                program.chat(message);
                return null;
            }
        };
        task.setOnFailed(_ -> {
            LOGGER.error("chat failed", task.getException());
            displaySystemMessage("something went wrong while generating this message");
        });
        Thread.startVirtualThread(task);
    }

    @Override
    public Client getClient() {
        return JavaFXLauncher.launchJavaFX();
    }
}