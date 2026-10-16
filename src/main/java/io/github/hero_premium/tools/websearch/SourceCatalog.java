package io.github.hero_premium.tools.websearch;

import java.util.*;

public final class SourceCatalog {

    private static final Wikipedia wikipedia = new Wikipedia();
    /// list containing sources for searching the model can use.
    public static final Map<String, Source> SOURCES;

    static {
        Map<String, Source> map = new HashMap<>();
        map.put(wikipedia.name, wikipedia);
        SOURCES = Collections.unmodifiableMap(map);
    }

    private static Optional<Source> getSource(String source) {
        source = source.toLowerCase(Locale.ROOT);
        return Optional.ofNullable(SOURCES.get(source));
    }

    /**
     * runs the webSearch and return the result, returns UNKNOWN SEARCH SOURCE: with the invalid search source name
     *
     * @param source the name of the source, sources found in {@link #SOURCES}
     * @param query  the search query used for the search
     * @return the search result, or the error message if something went wrong
     */
    public static String runWebSearch(String source, String query) {
        return getSource(source).map(search -> search.search(query)).orElseGet(() -> "UNKNOWN SEARCH SOURCE: " + source);
    }

    /**
     * don't instantiate this
     */
    private SourceCatalog() {
        throw new AssertionError("no io.github.hero_premium.tools.websearch.SourceCatalog instance for you!");
    }
}
