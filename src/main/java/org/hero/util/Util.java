package org.hero.util;

import com.google.gson.Gson;

import java.net.http.HttpClient;

public class Util {

    /**
     * used to turn Java code into JSON and vise versa
     */
    public static final Gson gson = new Gson();

    public static final HttpClient client = HttpClient.newHttpClient();

    private Util() {
        throw new AssertionError("no org.hero.util.Util instance for you!");
    }


}