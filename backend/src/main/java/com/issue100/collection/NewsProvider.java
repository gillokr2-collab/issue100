package com.issue100.collection;

import java.time.OffsetDateTime;
import java.util.List;

public interface NewsProvider {
    List<CollectedArticle> collectNews(NewsCollectionRequest request);

    record NewsCollectionRequest(OffsetDateTime since, int limit) {}
    record CollectedArticle(String externalArticleId, String title, String description,
        String originalUrl, String imageUrl, String publisher, OffsetDateTime publishedAt,
        String primaryCategory, String contentHash) {}
}
