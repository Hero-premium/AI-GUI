package org.hero.chatgui.javafx;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


import org.hero.Program;
import org.hero.Requests;
import org.hero.chatgui.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class JavaFX extends Application implements Client {

    private static final Logger LOGGER = LoggerFactory.getLogger(JavaFX.class);
    private final VBox root = new VBox();

    @Override
    public void start(Stage primaryStage) {
        Scene scene = new Scene(new Pane(), 800, 600);
        scene.setRoot(root);

        TextField textField = generateTextField();
        root.setAlignment(Pos.BOTTOM_CENTER);
        root.getChildren().add(textField);

        primaryStage.setScene(scene);
        primaryStage.setTitle("AI GUI");
        primaryStage.show();
    }

    @Override
    public void launchApplication() {
        launch();
    }

    @Override
    public void displayMessage(String message) {
        IO.println(message);
        List<Node> children = root.getChildren();
        children.add(children.size() - 1 , new GuiMessage(message).displayMessage());
    }

    @Override
    public void toolsDisplay(String tool) {
        IO.println("TOOLS [ " + tool + " ]");
        List<Node> children = root.getChildren();
        children.add(children.size() - 1 , new GuiMessage("TOOLS [ " + tool + " ]").displayMessage());
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
        displayMessage(message);
        Task<List<Requests.Message>> task = new Task<>() {
            @Override
            protected List<Requests.Message> call() {
                return Program.chat(message);
            }
        };
        task.setOnSucceeded(_ ->
                task.getValue().forEach(message1 -> displayMessage(message1.content()))
        );
        task.setOnFailed(_ -> LOGGER.error("chat failed", task.getException()));
        Thread.startVirtualThread(task);
    }
}
