package org.hero.tools.websearch;

import java.util.Objects;

public abstract class Source {

     /// the name of the source, used by the AI, names will always be all in lower case
    public final String name;

    protected Source(String name) {
        this.name = Objects.requireNonNull(name, "name must not be null").toLowerCase();
    }

    @Override
    public String toString() {
        return name;
    }

    public abstract String search(String query);
}
