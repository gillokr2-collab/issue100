package com.issue100.collection;

import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class MockNewsProvider implements NewsProvider {
    @Override
    public List<CollectedArticle> collectNews(NewsCollectionRequest request) {
        OffsetDateTime now = OffsetDateTime.now();
        return List.of(
            article(1, "전국 곳곳 폭염특보…온열질환 예방수칙 점검", "사회", now.minusMinutes(18)),
            article(2, "한국은행, 기준금리 결정 앞두고 시장 전망 엇갈려", "경제", now.minusMinutes(43)),
            article(3, "국내 연구진, 차세대 배터리 수명 개선 기술 공개", "IT·과학", now.minusHours(1)),
            article(4, "주말 남부지방 강한 비…산사태 위기경보 상향", "생활", now.minusHours(2)),
            article(5, "국회, 민생 법안 처리 일정 협의", "정치", now.minusHours(4))
        ).stream().limit(request.limit()).toList();
    }

    private CollectedArticle article(int id, String title, String category, OffsetDateTime publishedAt) {
        return new CollectedArticle("mock-" + id, title, title + " 관련 Mock 요약입니다.",
            "", "", "Mock 데이터", publishedAt, category, "mock-hash-" + id);
    }
}
