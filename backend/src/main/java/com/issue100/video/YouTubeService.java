package com.issue100.video;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.issue100.video.YouTubeController.PopularVideo;
import com.issue100.video.YouTubeController.PopularVideosResponse;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

@Service
public class YouTubeService {
    private final String apiKey;
    private final String region;
    private final ObjectMapper mapper;
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
    private volatile PopularVideosResponse cached;
    private volatile OffsetDateTime cachedAt;

    public YouTubeService(@Value("${youtube.api-key:}") String apiKey,
        @Value("${youtube.region:KR}") String region, ObjectMapper mapper) {
        this.apiKey = apiKey.trim();
        this.region = region;
        this.mapper = mapper;
    }

    public PopularVideosResponse popular() {
        if (apiKey.isBlank()) return new PopularVideosResponse(List.of(), false, region);
        OffsetDateTime now = OffsetDateTime.now();
        if (cached != null && cachedAt != null && cachedAt.isAfter(now.minusMinutes(10))) return cached;
        try {
            String url = "https://www.googleapis.com/youtube/v3/videos"
                + "?part=snippet,statistics&chart=mostPopular&regionCode=" + encode(region)
                + "&maxResults=6&key=" + encode(apiKey);
            HttpRequest request = HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(8)).GET().build();
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() / 100 != 2) return new PopularVideosResponse(List.of(), true, region);
            JsonNode root = mapper.readTree(response.body());
            List<PopularVideo> items = new ArrayList<>();
            for (JsonNode item : root.path("items")) {
                JsonNode snippet = item.path("snippet");
                String id = item.path("id").asText();
                String thumbnail = snippet.path("thumbnails").path("high").path("url").asText();
                if (thumbnail.isBlank()) thumbnail = snippet.path("thumbnails").path("medium").path("url").asText();
                items.add(new PopularVideo(id, HtmlUtils.htmlUnescape(snippet.path("title").asText()),
                    snippet.path("channelTitle").asText(), thumbnail,
                    "https://www.youtube.com/watch?v=" + id, snippet.path("publishedAt").asText(),
                    item.path("statistics").path("viewCount").asLong()));
            }
            cached = new PopularVideosResponse(items, true, region);
            cachedAt = now;
            return cached;
        } catch (Exception ignored) {
            return new PopularVideosResponse(List.of(), true, region);
        }
    }

    private String encode(String value) { return URLEncoder.encode(value, StandardCharsets.UTF_8); }
}
