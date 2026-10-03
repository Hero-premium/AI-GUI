package org.hero.tools;

public class WebSearch extends Tool {

    public WebSearch() {
        super("WebSearch", "used for search and browsing the internet", new Param("query", ToolsInformation.PropertiesType.STRING, "use this as the search query", true));
    }


    @Override
    protected String useTool() {
        return "trump is the current president";
    }
}
