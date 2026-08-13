package com.issue100.issue;

import com.issue100.issue.IssueModels.*;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class IssueController {
    private final IssueCatalogService catalog;
    public IssueController(IssueCatalogService catalog) { this.catalog = catalog; }

    @GetMapping("/issues/rankings")
    RankingResponse rankings(
        @RequestParam(defaultValue = "REALTIME") RankingType type,
        @RequestParam(required = false) String category,
        @RequestParam(defaultValue = "0") int offset,
        @RequestParam(defaultValue = "20") int limit) {
        List<IssueSummary> all = catalog.rankings(type, category);
        int from = Math.min(Math.max(0, offset), all.size());
        int to = Math.min(from + Math.min(Math.max(1, limit), 50), all.size());
        return new RankingResponse(all.subList(from, to), all.size(), type, category, OffsetDateTime.now());
    }
    @GetMapping("/issues/{issueId}") IssueDetail issue(@PathVariable long issueId) { return catalog.detail(issueId); }
    @GetMapping("/issues/{issueId}/articles") List<ArticleItem> articles(@PathVariable long issueId) { return catalog.articles(issueId); }
    @GetMapping("/issues/{issueId}/timeline") List<TimelineItem> timeline(@PathVariable long issueId) { return catalog.timeline(issueId); }
    @GetMapping("/categories") List<String> categories() { return catalog.categories(); }
}
