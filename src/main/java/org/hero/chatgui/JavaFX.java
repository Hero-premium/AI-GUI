package org.hero.chatgui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class JavaFX extends Application implements Client {
    @Override
    public void start(Stage primaryStage) {
        Scene scene = new Scene(new Pane(), 800, 600);

        primaryStage.setScene(scene);
        primaryStage.setTitle("My Program");
        primaryStage.show();
    }

    @Override
    public void launchApplication() {
        launch();
    }
}
