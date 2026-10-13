package org.hero.program;

import org.hero.chatgui.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private static final String DEFAULT_CLIENT = "org.hero.chatgui.javafx.JavaFX";

    static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        if (args.length == 0) {
            LOGGER.debug("No arguments provided");
            new Program(createClient(DEFAULT_CLIENT)).launch();
            return;
        }
        LOGGER.debug(args[0]);
        new Program(createClient(args[0])).launch();
    }

    private static Client createClient(String className) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Class<?> aClass = Class.forName(className);

        Class<? extends Client> subclass = aClass.asSubclass(Client.class);
        Constructor<? extends Client> constructor = subclass.getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance();
    }
}