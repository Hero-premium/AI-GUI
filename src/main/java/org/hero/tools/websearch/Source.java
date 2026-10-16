package org.hero.tools.websearch;

import java.util.Locale;
import java.util.Objects;

public abstract class Source {

    /// the name of the source, used by the AI, names will always be all in lower case
    public final String name;

    protected Source(String name) {
        this.name = Objects.requireNonNull(name, "name must not be null").toLowerCase(Locale.ROOT);
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Runs the search and returns the search result.
     *
     * @param query the search query must not be null.
     * @return the search result.
     */
    public abstract String search(String query);
}
