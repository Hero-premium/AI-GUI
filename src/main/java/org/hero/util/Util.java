package org.hero.util;

import com.google.gson.Gson;

public class Util {

    /**
     * used to turn Java code into JSON and vise versa
     */
    public static final Gson gson = new Gson();

    private Util() {
        throw new AssertionError("no org.hero.util.Util instance for you!");
    }


}