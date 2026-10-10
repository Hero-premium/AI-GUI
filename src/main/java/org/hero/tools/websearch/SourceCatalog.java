package org.hero.tools.websearch;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SourceCatalog {

    private static final Wikipedia wikipedia = new Wikipedia();
    /// list containing sources for searching the model can use.
    public static final Map<String, Source> SOURCES;

    static {
        Map<String, Source> map = new HashMap<>();
        map.put(wikipedia.name, wikipedia);
        SOURCES = Collections.unmodifiableMap(map);
    }

    private static Optional<Source> getSource(String source) {
        source = source.toLowerCase();
        return Optional.ofNullable(SOURCES.get(source));
    }

    public static String runWebSearch(String source, String query) {
        return getSource(source).map(search -> search.search(query)).orElseGet(() -> "UNKNOWN SEARCH SOURCE: " + source);
    }
}
