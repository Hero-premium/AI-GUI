package org.hero;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        Scene scene = new Scene(new Pane(), 800, 600);

        primaryStage.setScene(scene);
        primaryStage.setTitle("My Program");
        primaryStage.show();

        new ProgramLauncher();
    }

    @SuppressWarnings("unused")
    static void main(String[] args) {
        launch(args);
    }
}
