package org.hero.program;

import org.hero.chatgui.Client;
import org.hero.chatgui.javafx.JavaFX;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class Main {
    private static final Client DEFAULT_CLIENT = new JavaFX().getClient();

    static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        if (args.length == 0) {
            new Program(DEFAULT_CLIENT).launch();
            return;
        }
        new Program(createClient(args)).launch();
    }

    private static Client createClient(String[] args) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        String clientString = args[0];
        Class<?> aClass = Class.forName(clientString);

        Class<? extends Client> subclass = aClass.asSubclass(Client.class);
        Constructor<? extends Client> constructor = subclass.getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance().getClient();
    }
}