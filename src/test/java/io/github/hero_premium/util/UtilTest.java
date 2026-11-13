package io.github.hero_premium.util;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UtilTest {

    @Test
    void constructorThrows() throws NoSuchMethodException {
        Constructor<Util> constructor = Util.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        Throwable thrown = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertInstanceOf(AssertionError.class, thrown.getCause());
    }

}