package com.issue100.search;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class DefaultSearchKeywordNormalizerTest {
    private final DefaultSearchKeywordNormalizer normalizer = new DefaultSearchKeywordNormalizer();
    @Test void mergesAliasesAndRemovesSymbols() {
        assertThat(normalizer.normalize("  삼성 전자!! ")).isEqualTo("삼성전자");
    }
    @Test void filtersPrivateData() {
        assertThat(normalizer.normalize("010-1234-5678")).isEmpty();
        assertThat(normalizer.normalize("person@example.com")).isEmpty();
    }
}
