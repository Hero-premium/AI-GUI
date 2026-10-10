package org.hero.tools.websearch;

import org.hero.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.rmi.ServerException;
import java.time.Duration;
import java.util.Map;

public class Wikipedia extends Source {

    /**
     * Gson mapping for the response of Wikipedia's {@code action=query} API
     * when used with {@code generator=search} and {@code prop=extracts}.
     * Only the fields this project uses are declared; Gson ignores the rest.
     */
    record WikipediaResponse(Query query) {

        /**
         * The {@code query} object of the response.
         *
         * @param pages search hits keyed by page ID. Map order is not search
         *              order, so sort by {@link Page#index()} before use.
         */
        record Query(Map<String, Page> pages) {
        }

        /**
         * A single search hit.
         *
         * @param title   article title
         * @param extract plain-text intro of the article, may be empty
         *                (e.g. disambiguation pages) and may contain {@code \n}
         * @param index   search rank, 1 is the best match
         */
        record Page(String title, String extract, int index) {
        }
    }

    private static final String LINK =
            "https://en.wikipedia.org/w/api.php?action=query&generator=search&gsrlimit=3&prop=extracts&exintro=1&explaintext=1&exlimit=max&format=json&gsrsearch=";

    private static final Logger LOGGER = LoggerFactory.getLogger(Wikipedia.class);

    Wikipedia() {
        super("Wikipedia");
    }


    @Override
    public String search(String query) {
        String result;
        try {
            HttpResponse<String> response = Util.client.send(buildRequest(query), HttpResponse.BodyHandlers.ofString());
            StringBuilder stringBuilder = new StringBuilder();
            Util.gson.fromJson(response.body(), WikipediaResponse.class).query().pages().values().forEach( page -> stringBuilder.append(page.extract).append("\n"));
            result = stringBuilder.toString();
        } catch (ServerException _) {
            result = "server error";
        } catch (IOException | InterruptedException e) {
            result = "something went wrong";
            LOGGER.error(result, e.getMessage(), e);
        }
        return result;
    }

    private HttpRequest buildRequest(String query) {
        return HttpRequest.newBuilder()
                .header("User-Agent", "AI-GUI/0.1 (https://github.com/Hero-premium/AI-GUI)")
                .timeout(Duration.ofSeconds(15))
                .uri(URI.create(LINK + URLEncoder.encode(query, StandardCharsets.UTF_8)))
                .build();
    }
}