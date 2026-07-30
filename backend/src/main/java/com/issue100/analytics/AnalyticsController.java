package com.issue100.analytics;

import com.issue100.analytics.EventModels.*;
import com.issue100.issue.IssueCatalogService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class AnalyticsController {
    private final AnalyticsService analytics;
    private final IssueCatalogService catalog;
    public AnalyticsController(AnalyticsService analytics, IssueCatalogService catalog) {
        this.analytics = analytics; this.catalog = catalog;
    }
    @PostMapping("/issues/{issueId}/views")
    EventResponse view(@PathVariable long issueId, @Valid @RequestBody ViewEventRequest request) {
        catalog.issue(issueId); return analytics.view(issueId, request);
    }
    @PostMapping("/issues/{issueId}/read-complete")
    EventResponse read(@PathVariable long issueId) { catalog.issue(issueId); return analytics.readComplete(); }
    @PostMapping("/issues/{issueId}/scroll-depth")
    EventResponse scroll(@PathVariable long issueId, @Valid @RequestBody ScrollEventRequest request) {
        catalog.issue(issueId); return analytics.scroll(request);
    }
    @PostMapping("/articles/{articleId}/outbound-click")
    EventResponse outbound(@PathVariable long articleId, @Valid @RequestBody OutboundClickRequest request) {
        catalog.article(articleId); return analytics.outbound();
    }
}
