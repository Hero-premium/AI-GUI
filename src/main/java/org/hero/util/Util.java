package org.hero.util;

import com.google.gson.Gson;

public class Util {


    public static final Gson gson = new Gson();

    private Util() {
        throw new AssertionError("no  org.hero.util.Util instance for you!");
    }


}
