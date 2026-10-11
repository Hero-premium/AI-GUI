package org.hero.tools.websearch;

import org.hero.util.Util;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WikipediaTest {

    @Test
    void filterAndBuildResponse() {
        String page;
        String result;
        page = Util.gson.toJson(
                new Wikipedia.WikipediaResponse(
                        new Wikipedia.WikipediaResponse.Query(
                                Map.of("unimportant",
                                        new Wikipedia.WikipediaResponse.Page("", "tooShort", 1)))));
        result = Wikipedia.filterAndBuildResponse(page);
        assertEquals("no results were found", result);
        String extract = """
                hi i think this is a very good chance for me to talk a bit because i need 50 litters for the test,
                 i think i got them bye""";

        page = Util.gson.toJson(
                new Wikipedia.WikipediaResponse(
                        new Wikipedia.WikipediaResponse.Query(
                                Map.of("unimportant",
                                        new Wikipedia.WikipediaResponse.Page("title", extract, 1)))));
        result = Wikipedia.filterAndBuildResponse(page);
        assertEquals("title\n\n" + extract + "\n", result);

        page = Util.gson.toJson(
                new Wikipedia.WikipediaResponse(
                        new Wikipedia.WikipediaResponse.Query(
                                Map.of("un]",
                                        new Wikipedia.WikipediaResponse.Page("title1", extract, 1),
                                        "hi",
                                        new Wikipedia.WikipediaResponse.Page("title2", extract, 2),
                                        "import",
                                        new Wikipedia.WikipediaResponse.Page("title3", extract, 3)))));

        result = Wikipedia.filterAndBuildResponse(page);
        assertEquals("title1\n\n" + extract + "\ntitle2\n\n" + extract + "\n", result);
    }
}