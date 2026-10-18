package io.github.hero_premium.tools.websearch;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SourceCatalogTest {

    @Test
    void runWebSearch() {
        String result;

        result = SourceCatalog.runWebSearch("source", "null");
        assertEquals("INVALID SEARCH SOURCE: source", result);
    }
}