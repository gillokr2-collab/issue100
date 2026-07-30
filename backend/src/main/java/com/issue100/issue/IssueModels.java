package com.issue100.issue;

import java.time.OffsetDateTime;
import java.util.List;

public final class IssueModels {
    private IssueModels() {}

    public enum RankingType { REALTIME, RISING, TODAY, WEEKLY }
    public enum RankStatus { UP, DOWN, SAME, NEW }
    public enum EntityType { PERSON, COMPANY, ORGANIZATION, LOCATION, PRODUCT }

    public record IssueSummary(
        long id, int rank, int previousRank, RankStatus rankStatus, String category,
        String title, String aiSummary, double interestScore, int articleCount,
        int publisherCount, int pageViews, int uniqueViews, int outboundClicks,
        int searchCount, int articleVelocity, OffsetDateTime updatedAt, List<String> tags) {}

    public record IssueDetail(
        IssueSummary issue, List<String> keyFacts, List<EntityItem> entities,
        List<Long> relatedIssueIds) {}

    public record ArticleItem(long id, long issueId, String publisher, String title,
        String originalUrl, OffsetDateTime publishedAt, boolean officialSource) {}

    public record TimelineItem(long id, long issueId, OffsetDateTime eventTime,
        String title, String description, int displayOrder) {}

    public record EntityItem(EntityType type, String name) {}

    public record RankingResponse(List<IssueSummary> items, int total,
        RankingType type, String category, OffsetDateTime calculatedAt) {}
}
