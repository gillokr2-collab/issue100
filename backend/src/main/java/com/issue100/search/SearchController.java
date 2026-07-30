package com.issue100.search;

import com.issue100.search.SearchModels.*;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/search")
public class SearchController {
    private final SearchService service;
    public SearchController(SearchService service) { this.service = service; }
    @GetMapping SearchResponse search(@RequestParam String query) { return service.search(query); }
    @PostMapping("/events") SearchEventResponse event(@Valid @RequestBody SearchEventRequest request) { return service.record(request); }
    @GetMapping("/rankings/realtime") SearchRankingResponse ranking() { return service.realtimeRanking(); }
}
