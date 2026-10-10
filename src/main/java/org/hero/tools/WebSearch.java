package org.hero.tools;

import org.hero.tools.websearch.SourceCatalog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static org.hero.tools.websearch.SourceCatalog.SOURCES;

public class WebSearch extends Tool {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSearch.class);

    public WebSearch() {
        super("WebSearch", "use only when the user explicitly asks you to fetch information from the internet"
                , new Param("query", PropertiesType.STRING,
                        "the query that will be used to get your search result back", true)
                , new Param("source", PropertiesType.STRING, "the source of the search result, the sources are " + SOURCES.keySet(), true));
    }


    @Override
    protected String useTool(Map<String, Object> arguments) {
        // casting is safe because the validation was done a layer earlier.
        String query = (String) arguments.get("query");
        String source = (String) arguments.get("source");

        return SourceCatalog.runWebSearch(source, query);
    }
}