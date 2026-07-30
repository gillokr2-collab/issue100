package com.issue100.analytics;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public final class EventModels {
    private EventModels() {}
    public enum EventType { PAGE_VIEW, UNIQUE_VIEW, OUTBOUND_CLICK, READ_COMPLETE, SCROLL_DEPTH }
    public record ViewEventRequest(@NotBlank String visitorId, @NotBlank String sessionId,
        @Min(2) int durationSeconds) {}
    public record ScrollEventRequest(@NotBlank String visitorId, @NotBlank String sessionId,
        @Min(0) @Max(100) int scrollDepth) {}
    public record OutboundClickRequest(@NotBlank String visitorId, @NotBlank String sessionId) {}
    public record EventResponse(boolean accepted, boolean unique, EventType eventType) {}
}
