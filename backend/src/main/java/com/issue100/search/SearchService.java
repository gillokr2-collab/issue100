package com.issue100.search;

import com.issue100.issue.IssueCatalogService;
import com.issue100.issue.IssueModels.ArticleItem;
import com.issue100.issue.IssueModels.IssueSummary;
import com.issue100.search.SearchModels.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class SearchService {
    private final IssueCatalogService catalog;
    private final SearchKeywordNormalizer normalizer;
    private final Map<String, OffsetDateTime> deduplication = new ConcurrentHashMap<>();
    private final Map<String, Integer> counts = new ConcurrentHashMap<>();
    private volatile List<SearchRanking> snapshot = List.of();
    private volatile List<SearchRanking> previous = List.of();

    public SearchService(IssueCatalogService catalog, SearchKeywordNormalizer normalizer) {
        this.catalog = catalog; this.normalizer = normalizer;
        String[] mock = {"수도권 폭우", "삼성전자 AI폰", "프로야구 순위", "기준금리", "태풍 경로",
            "부동산 정책", "비트코인", "축구 국가대표", "배우 결혼", "신작 영화",
            "배터리 기술", "국회 일정", "폭염 건강", "아시아 증시", "기후 정상회의",
            "서울 교통", "AI 스마트폰", "주말 날씨", "한국 영화", "온열질환"};
        for (int i = 0; i < mock.length; i++) counts.put(mock[i], 220 - i * 9);
        calculateSnapshot();
    }

    public SearchResponse search(String raw) {
        String query = normalizer.normalize(raw);
        if (query.isBlank()) return new SearchResponse(raw, "", List.of(), List.of(), 0);
        String compact = query.replace(" ", "");
        List<IssueSummary> issues = catalog.allIssues().stream().filter(issue ->
            searchable(issue.title(), issue.aiSummary(), String.join(" ", issue.tags()))
                .replace(" ", "").contains(compact)).toList();
        Set<Long> ids = issues.stream().map(IssueSummary::id).collect(java.util.stream.Collectors.toSet());
        List<ArticleItem> articles = catalog.allArticles().stream().filter(article ->
            article.title().toLowerCase().replace(" ", "").contains(compact) || ids.contains(article.issueId()))
            .limit(20).toList();
        return new SearchResponse(raw, query, issues, articles, issues.size() + articles.size());
    }

    public SearchEventResponse record(SearchEventRequest request) {
        String normalized = normalizer.normalize(request.keyword());
        if (normalized.isBlank()) return new SearchEventResponse(false, normalized, false);
        SearchResponse result = search(normalized);
        if (result.total() == 0) return new SearchEventResponse(true, normalized, false);
        String key = request.visitorId() + ":" + normalized;
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime previousEvent = deduplication.put(key, now);
        boolean counted = previousEvent == null || previousEvent.isBefore(now.minusMinutes(10));
        if (counted) counts.merge(normalized, 1, Integer::sum);
        return new SearchEventResponse(true, normalized, counted);
    }

    public SearchRankingResponse realtimeRanking() {
        return new SearchRankingResponse(snapshot, OffsetDateTime.now().withSecond(0).withNano(0));
    }

    @Scheduled(fixedRate = 300_000)
    void calculateSnapshot() {
        previous = snapshot;
        List<Map.Entry<String, Integer>> top = counts.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed()).limit(10).toList();
        snapshot = java.util.stream.IntStream.range(0, top.size()).mapToObj(index -> {
            String keyword = top.get(index).getKey();
            int previousRank = java.util.stream.IntStream.range(0, previous.size())
                .filter(i -> previous.get(i).keyword().equals(keyword)).map(i -> i + 1).findFirst().orElse(0);
            int rank = index + 1;
            RankStatus status = previousRank == 0 ? RankStatus.NEW :
                previousRank > rank ? RankStatus.UP : previousRank < rank ? RankStatus.DOWN : RankStatus.SAME;
            return new SearchRanking(rank, previousRank, status,
                previousRank == 0 ? 0 : Math.abs(previousRank - rank), keyword, top.get(index).getValue());
        }).toList();
    }

    private String searchable(String... values) { return String.join(" ", values).toLowerCase(); }
}
