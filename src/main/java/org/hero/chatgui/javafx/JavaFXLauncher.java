package org.hero.chatgui.javafx;

import javafx.application.Application;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaFXLauncher extends Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(JavaFXLauncher.class);
    private volatile static JavaFX javaFX;
    public static final Object lock = new Object();
    private static boolean launched = false;

    public static JavaFX launchJavaFX() {
        if (!launched) {
            new Thread(() -> launch(JavaFXLauncher.class), "javafx-launcher").start();
            launched = true;
        }
        synchronized (lock) {
            while (javaFX == null) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }
            }
        }
        return javaFX;
    }

    @Override
    public void start(Stage primaryStage) {
        LOGGER.debug("launched");
        javaFX = new JavaFX(primaryStage);
        synchronized (lock) {
            lock.notifyAll();
        }
    }
}
