package com.issue100.search;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class DefaultSearchKeywordNormalizer implements SearchKeywordNormalizer {
    private static final Pattern SPECIAL = Pattern.compile("[^\\p{L}\\p{N}\\s]");
    private static final Pattern PRIVATE_DATA = Pattern.compile(
        "(\\d{2,3}-?\\d{3,4}-?\\d{4})|([\\w.%+-]+@[\\w.-]+\\.[A-Za-z]{2,})");
    private static final Set<String> BLOCKED = Set.of("금칙어", "불법광고");

    @Override
    public String normalize(String keyword) {
        if (keyword == null) return "";
        String value = Normalizer.normalize(keyword, Normalizer.Form.NFKC)
            .toLowerCase(Locale.ROOT).trim();
        value = SPECIAL.matcher(value).replaceAll("");
        value = value.replaceAll("\\s+", " ");
        String canonical = value.replace("삼성 전자", "삼성전자");
        if (canonical.length() < 2 || BLOCKED.contains(canonical)
            || PRIVATE_DATA.matcher(canonical).find()) return "";
        return canonical;
    }
}
