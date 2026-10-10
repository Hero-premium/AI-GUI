package org.hero.tools.websearch;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SourceCatalogTest {

    @Test
    void runWebSearch() {
        String result;

        result = SourceCatalog.runWebSearch("source", "null");
        assertEquals("UNKNOWN SEARCH SOURCE: source", result);
    }
}