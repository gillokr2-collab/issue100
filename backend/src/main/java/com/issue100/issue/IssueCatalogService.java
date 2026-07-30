package com.issue100.issue;

import com.issue100.collection.MockNewsProvider;
import com.issue100.collection.NewsProvider.CollectedArticle;
import com.issue100.collection.NewsProvider.NewsCollectionRequest;
import com.issue100.collection.RssNewsProvider;
import com.issue100.issue.IssueModels.*;
import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class IssueCatalogService {
    private static final Logger log = LoggerFactory.getLogger(IssueCatalogService.class);
    private static final Duration REALTIME_WINDOW = Duration.ofHours(6);
    private static final double HALF_LIFE_HOURS = 3.0;

    private final RssNewsProvider rss;
    private final MockNewsProvider mock;
    private final List<IssueSummary> issues = new ArrayList<>();
    private final List<ArticleItem> articles = new ArrayList<>();
    private final List<TimelineItem> timelines = new ArrayList<>();

    public IssueCatalogService(RssNewsProvider rss, MockNewsProvider mock) {
        this.rss = rss;
        this.mock = mock;
    }

    @PostConstruct
    void initialize() {
        OffsetDateTime now = OffsetDateTime.now();
        List<CollectedArticle> collected;
        try {
            collected = rss.collectNews(new NewsCollectionRequest(now.minusDays(7), 100));
            log.info("KBS RSS 기사 {}건을 수집했습니다.", collected.size());
        } catch (RuntimeException failure) {
            log.warn("RSS 수집 실패로 Mock 데이터를 사용합니다: {}", failure.getMessage());
            collected = mock.collectNews(new NewsCollectionRequest(now.minusDays(7), 100));
        }
        AtomicLong id = new AtomicLong();
        for (CollectedArticle source : collected) {
            long issueId = id.incrementAndGet();
            double score = decayedScore(source.publishedAt(), now);
            issues.add(new IssueSummary(issueId, (int) issueId, 0, RankStatus.NEW,
                source.primaryCategory(), source.title(),
                source.description().isBlank() ? source.title() : source.description(),
                score, 1, 1, 0, 0, 0, 0, 1, source.publishedAt(),
                List.of(source.primaryCategory(), "RSS", source.publisher())));
            articles.add(new ArticleItem(issueId, issueId, source.publisher(), source.title(),
                source.originalUrl(), source.publishedAt(), "KBS".equals(source.publisher())));
            timelines.add(new TimelineItem(issueId, issueId, source.publishedAt(),
                "기사 최초 보도", source.title(), 1));
        }
    }

    public List<IssueSummary> rankings(RankingType type, String category) {
        OffsetDateTime now = OffsetDateTime.now();
        Comparator<IssueSummary> comparator = switch (type) {
            case RISING, REALTIME -> Comparator.comparingDouble(
                (IssueSummary issue) -> decayedScore(issue.updatedAt(), now)).reversed();
            case TODAY -> Comparator.comparing(IssueSummary::updatedAt).reversed();
            case WEEKLY -> Comparator.comparingDouble(IssueSummary::interestScore).reversed();
        };
        var stream = issues.stream()
            .filter(issue -> category == null || category.isBlank()
                || issue.category().equalsIgnoreCase(category));
        if (type == RankingType.REALTIME || type == RankingType.RISING) {
            OffsetDateTime cutoff = now.minus(REALTIME_WINDOW);
            stream = stream.filter(issue -> !issue.updatedAt().isBefore(cutoff));
        }
        List<IssueSummary> result = stream.sorted(comparator).toList();
        return IntStream.range(0, result.size())
            .mapToObj(index -> withRankAndScore(result.get(index), index + 1,
                type == RankingType.REALTIME || type == RankingType.RISING
                    ? decayedScore(result.get(index).updatedAt(), now)
                    : result.get(index).interestScore()))
            .toList();
    }

    static double decayedScore(OffsetDateTime publishedAt, OffsetDateTime now) {
        double ageHours = Math.max(0, Duration.between(publishedAt, now).toMinutes() / 60.0);
        return Math.round(100.0 * Math.pow(0.5, ageHours / HALF_LIFE_HOURS) * 10.0) / 10.0;
    }

    public IssueDetail detail(long id) {
        IssueSummary issue = issue(id);
        return new IssueDetail(issue,
            List.of("공식 RSS에서 제공한 제목과 요약입니다.", "정확한 내용은 연결된 원문에서 확인해 주세요."),
            List.of(new EntityItem(EntityType.ORGANIZATION, articles(id).getFirst().publisher())),
            issues.stream().filter(item -> item.id() != id).limit(3).map(IssueSummary::id).toList());
    }

    public IssueSummary issue(long id) {
        return issues.stream().filter(issue -> issue.id() == id).findFirst()
            .orElseThrow(() -> new IllegalArgumentException("이슈를 찾을 수 없습니다."));
    }
    public List<ArticleItem> articles(long issueId) { issue(issueId); return articles.stream().filter(a -> a.issueId() == issueId).toList(); }
    public List<ArticleItem> allArticles() { return List.copyOf(articles); }
    public ArticleItem article(long id) { return articles.stream().filter(a -> a.id() == id).findFirst().orElseThrow(() -> new IllegalArgumentException("기사를 찾을 수 없습니다.")); }
    public List<TimelineItem> timeline(long issueId) { issue(issueId); return timelines.stream().filter(t -> t.issueId() == issueId).toList(); }
    public List<String> categories() { return issues.stream().map(IssueSummary::category).distinct().toList(); }
    public List<IssueSummary> allIssues() { return List.copyOf(issues); }

    private IssueSummary withRankAndScore(IssueSummary i, int rank, double score) {
        return new IssueSummary(i.id(), rank, i.previousRank(), i.rankStatus(), i.category(),
            i.title(), i.aiSummary(), score, i.articleCount(), i.publisherCount(),
            i.pageViews(), i.uniqueViews(), i.outboundClicks(), i.searchCount(),
            i.articleVelocity(), i.updatedAt(), i.tags());
    }
}
