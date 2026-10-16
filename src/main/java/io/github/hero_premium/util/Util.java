package io.github.hero_premium.util;

import com.google.gson.Gson;

import java.net.http.HttpClient;

public final class Util {

    /**
     * used to turn Java code into JSON and vise versa
     */
    public static final Gson gson = new Gson();

    public static final HttpClient client = HttpClient.newHttpClient();

    private Util() {
        throw new AssertionError("no io.github.hero_premium.utilUtil instance for you!");
    }

}