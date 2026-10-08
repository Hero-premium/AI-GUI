package org.hero.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class WebSearch extends Tool {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSearch.class);

    public WebSearch() {
        super("WebSearch", "use only when the user explicitly asks you to fetch an information from the internet"
                , new Param("query", PropertiesType.STRING,
                        "the query that will be used to get your search result back", true));
    }

    // this will become a real search tool at some point
    @Override
    protected String useTool(Map<String, Object> tools) {
        return "TOOL ERROR, SOMETHING WENT WRONG -> java.lang.IllegalAccessException tool is bugged, don't use it ";
    }
}
