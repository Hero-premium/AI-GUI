package io.github.hero_premium.tools.websearch;

import com.google.gson.JsonSyntaxException;
import io.github.hero_premium.util.Util;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Wikipedia extends Source {

    /**
     * Gson mapping for the response of Wikipedia's {@code action=query} API
     * when used with {@code generator=search} and {@code prop=extracts}.
     * Only the fields this project uses are declared; Gson ignores the rest.
     */
    @SuppressWarnings("ConstantConditions")
    record WikipediaResponse(@NotNull Query query) {
        WikipediaResponse {
            if (query == null) query = new Query(Map.of());
        }

        /**
         * The {@code query} object of the response.
         *
         * @param pages search hits keyed by page ID. Map order is not search
         *              order, so sort by {@link Page#index()} before use.
         */
        record Query(@NotNull Map<String, Page> pages) {
            Query {
                if (pages == null) pages = Map.of();
            }
        }

        /**
         * A single search hit.
         *
         * @param title   article title
         * @param extract plain-text intro of the article, may be empty
         *                (e.g. disambiguation pages) and may contain {@code \n}
         * @param index   search rank, 1 is the best match
         */
        record Page(@NotNull String title, @NotNull String extract, int index) {
            Page {
                if (title == null) title = "";
                if (extract == null) extract = "";
            }
        }
    }

    private static final String LINK =
            "https://en.wikipedia.org/w/api.php?action=query&generator=search&gsrlimit=3&prop=extracts&exintro=1&explaintext=1&exlimit=max&format=json&gsrsearch=";

    private static final Logger LOGGER = LoggerFactory.getLogger(Wikipedia.class);

    Wikipedia() {
        super("Wikipedia");
    }

    /**
     * performs the searching, handles formating, error handling, and safety of the response
     *
     * @param query the query to search about
     * @return the result of the search, usually 2 or 3 paragraphs
     */
    @Override
    public String search(String query) {
        String result = "something went wrong";
        try {
            HttpResponse<String> response = Util.client.send(buildRequest(query), HttpResponse.BodyHandlers.ofString());
            result = filterAndBuildResponse(response.body());
        } catch (ConnectException _) {
            result = "couldn’t reach Wikipedia";
        } catch (HttpTimeoutException _) {
            result = "connection timed out";
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.error(e.getMessage(), e);
        } catch (IOException | JsonSyntaxException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return result;
    }


    static @NotNull String filterAndBuildResponse(String response) {
        String result = Util.gson.fromJson(response, WikipediaResponse.class)
                .query().pages().values()
                .stream()
                .filter(page -> page.extract().length() >= 50)
                .sorted(Comparator.comparingInt(WikipediaResponse.Page::index))
                .limit(2)
                .map(Wikipedia::appendResponse)
                .collect(Collectors.joining());

        return result.isEmpty() ? "no results were found" : result;
    }

    private static @NotNull String appendResponse(WikipediaResponse.@NotNull Page page) {
        return page.title() + "\n\n" + page.extract() + "\n";
    }


    private HttpRequest buildRequest(@NotNull String query) {
        Objects.requireNonNull(query, "query cannot be null");
        return HttpRequest.newBuilder()
                .header("User-Agent", "AI-GUI/0.1 (https://github.com/Hero-premium/AI-GUI)")
                .timeout(Duration.ofSeconds(15))
                .uri(URI.create(LINK + URLEncoder.encode(query, StandardCharsets.UTF_8)))
                .build();
    }
}