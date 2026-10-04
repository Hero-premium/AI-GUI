package org.hero.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class WebSearch extends Tool {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSearch.class);
    private static final Param param1 = new Param("query", ToolsInformation.PropertiesType.STRING,
            "the query that will be used to get your search result back", true);


    public WebSearch() {
        super("WebSearch", "use only when the user explicitly asks you to fetch an information from the internet", param1);
    }

    @Override
    protected String useTool(Map<String, Object> tools) {
        if (tools != null && tools.get(param1.argumentName()) instanceof String) {
            return "trump is the current president";
        }
        LOGGER.warn("ai misused the tool, TOOL ERROR, EXPECTED STRING");
        return "TOOL ERROR, EXPECTED STRING";
    }
}
