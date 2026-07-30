package com.issue100.analytics;

import com.issue100.analytics.EventModels.*;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class AnalyticsService {
    private final Map<String, OffsetDateTime> uniqueViews = new ConcurrentHashMap<>();

    public EventResponse view(long issueId, ViewEventRequest request) {
        boolean validDuration = request.durationSeconds() >= 2;
        if (!validDuration) return new EventResponse(false, false, EventType.PAGE_VIEW);
        String key = request.visitorId() + ":" + issueId;
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime last = uniqueViews.put(key, now);
        boolean unique = last == null || last.isBefore(now.minusMinutes(30));
        return new EventResponse(true, unique, unique ? EventType.UNIQUE_VIEW : EventType.PAGE_VIEW);
    }
    public EventResponse readComplete() { return new EventResponse(true, false, EventType.READ_COMPLETE); }
    public EventResponse scroll(ScrollEventRequest request) { return new EventResponse(true, false, EventType.SCROLL_DEPTH); }
    public EventResponse outbound() { return new EventResponse(true, false, EventType.OUTBOUND_CLICK); }
}
