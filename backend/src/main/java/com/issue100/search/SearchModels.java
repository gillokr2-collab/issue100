package com.issue100.search;

import com.issue100.issue.IssueModels.ArticleItem;
import com.issue100.issue.IssueModels.IssueSummary;
import jakarta.validation.constraints.NotBlank;
import java.time.OffsetDateTime;
import java.util.List;

public final class SearchModels {
    private SearchModels() {}
    public enum RankStatus { UP, DOWN, SAME, NEW }
    public record SearchResponse(String query, String normalizedQuery,
        List<IssueSummary> issues, List<ArticleItem> articles, int total) {}
    public record SearchEventRequest(@NotBlank String keyword, @NotBlank String visitorId,
        @NotBlank String sessionId, boolean resultClicked) {}
    public record SearchEventResponse(boolean accepted, String normalizedKeyword, boolean counted) {}
    public record SearchRanking(int rank, int previousRank, RankStatus status,
        int rankChange, String keyword, int searchCount) {}
    public record SearchRankingResponse(List<SearchRanking> items, OffsetDateTime calculatedAt) {}
}
