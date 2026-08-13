package com.issue100.video;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/videos")
public class YouTubeController {
    private final YouTubeService service;

    public YouTubeController(YouTubeService service) { this.service = service; }

    @GetMapping("/popular")
    PopularVideosResponse popular() { return service.popular(); }

    public record PopularVideo(String id, String title, String channelTitle,
        String thumbnailUrl, String videoUrl, String publishedAt, long viewCount) {}
    public record PopularVideosResponse(List<PopularVideo> items, boolean configured, String region) {}
}
