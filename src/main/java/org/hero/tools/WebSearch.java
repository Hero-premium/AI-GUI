package org.hero.tools;

import java.util.Map;

public class WebSearch extends Tool {

    public WebSearch() {
        super("WebSearch", "", new Param("query", ToolsInformation.PropertiesType.STRING, "", true));
    }

    @Override
    protected String useTool(Map<String, Object> tools) {
        return "trump is the current president";
    }
}
