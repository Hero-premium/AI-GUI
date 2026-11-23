package io.github.hero_premium.tools;

import io.github.hero_premium.tools.websearch.SourceCatalog;

import java.util.Map;

import static io.github.hero_premium.tools.websearch.SourceCatalog.SOURCES;

public class WebSearch extends Tool {

    public WebSearch() {
        super("WebSearch", "use only when the user explicitly asks you to fetch information from the internet"
                , new Param("query", PropertyType.STRING,
                        "the query that will be used to get your search result back", true)
                , new Param("source", PropertyType.STRING, "the source of the search result, the sources are " + SOURCES.keySet() +
                        " you must select one of those sources, any other source will be rejected", true));
    }


    @Override
    protected String useTool(Map<String, Object> arguments) {
        // casting is safe because the validation was done a layer earlier.
        String query = (String) arguments.get("query");
        String source = (String) arguments.get("source");

        return SourceCatalog.runWebSearch(source, query);
    }
}