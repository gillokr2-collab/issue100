INSERT INTO publisher(name, domain)
SELECT 'Mock 언론사 ' || n, 'mock' || n || '.example.com'
FROM generate_series(1, 12) AS n;

INSERT INTO issue(title, slug, summary, primary_category, interest_score, current_rank,
 previous_rank, rank_change, first_reported_at, last_updated_at, total_article_count,
 publisher_count, total_page_views, total_unique_views, total_outbound_clicks, total_search_count)
SELECT
 CASE n
  WHEN 1 THEN '수도권 집중호우, 도로 통제와 지하철 지연'
  WHEN 2 THEN '삼성전자, 온디바이스 AI 강화한 신형 스마트폰 공개'
  WHEN 3 THEN '손흥민 새 시즌 거취에 유럽 현지 언론 집중'
  ELSE 'Mock 실시간 주요 이슈 ' || n END,
 'mock-issue-' || n,
 '여러 공개 출처에서 공통 사실을 추출한 AI Mock 핵심 요약입니다.',
 (ARRAY['POLITICS','ECONOMY','SOCIETY','WORLD','ENTERTAINMENT','SPORTS','TECH','LIFE','CULTURE','HEALTH'])[((n-1)%10)+1],
 ROUND((96 - n * 2.1)::numeric, 1), n, CASE WHEN n % 5 = 0 THEN NULL ELSE n + 1 END,
 CASE WHEN n % 5 = 0 THEN 0 ELSE 1 END,
 NOW() - (n || ' hours')::interval, NOW() - (n * 7 || ' minutes')::interval,
 4, LEAST(12, 4 + n), 13000 - n * 300, 9000 - n * 200, 800 - n * 15, 1000 - n * 25
FROM generate_series(1, 15) AS n;

INSERT INTO article(external_article_id, title, description, original_url, publisher_id,
 published_at, collected_at, source_type, official_source, primary_category, content_hash)
SELECT 'mock-article-' || n,
 CASE ((n-1)/4)+1
  WHEN 1 THEN (ARRAY['서울 폭우로 도로 통제','수도권 집중호우 피해 속출','서울 시간당 80㎜ 폭우','폭우로 지하철 일부 지연'])[((n-1)%4)+1]
  ELSE 'Mock 기사 제목 ' || n || ' — 같은 사건의 서로 다른 표현' END,
 '저작권 보호를 위해 본문 전체가 아닌 Mock 설명만 저장합니다.',
 'https://example.com/news/' || n, ((n-1)%12)+1,
 NOW() - (n * 11 || ' minutes')::interval, NOW(),
 'MOCK', n % 13 = 0, 'MOCK', md5('mock-content-' || n)
FROM generate_series(1, 60) AS n;

INSERT INTO issue_article(issue_id, article_id, similarity_score, representative)
SELECT ((n-1)/4)+1, n, 0.78 + ((n%4)::numeric / 20), n % 4 = 1
FROM generate_series(1, 60) AS n;

INSERT INTO issue_timeline(issue_id, event_time, title, description, display_order)
SELECT issue_id, NOW() - ((4-display_order) || ' hours')::interval,
 (ARRAY['최초 보도','공식 발표','후속 상황 업데이트'])[display_order],
 'Mock 타임라인 설명입니다.', display_order
FROM generate_series(1,15) issue_id CROSS JOIN generate_series(1,3) display_order;

INSERT INTO search_event(keyword, normalized_keyword, canonical_keyword, visitor_id,
 session_id, result_count, result_clicked, valid_search, device_type, searched_at)
SELECT (ARRAY['수도권 폭우','삼성전자 AI폰','손흥민 이적','기준금리','태풍 경로',
 '부동산 정책','비트코인','축구 국가대표','배우 결혼','신작 영화'])[((n-1)%10)+1],
 (ARRAY['수도권 폭우','삼성전자 ai폰','손흥민 이적','기준금리','태풍 경로',
 '부동산 정책','비트코인','축구 국가대표','배우 결혼','신작 영화'])[((n-1)%10)+1],
 (ARRAY['수도권 폭우','삼성전자 AI폰','손흥민 이적','기준금리','태풍 경로',
 '부동산 정책','비트코인','축구 국가대표','배우 결혼','신작 영화'])[((n-1)%10)+1],
 'mock-visitor-' || ((n-1)%80), 'mock-session-' || ((n-1)%100),
 3 + (n%12), n%3=0, TRUE, 'MOBILE', NOW() - (n%60 || ' minutes')::interval
FROM generate_series(1, 220) AS n;

INSERT INTO issue_view_event(issue_id, visitor_id, session_id, event_type,
 duration_seconds, scroll_depth, valid_event, viewed_at)
SELECT ((n-1)%15)+1, 'mock-visitor-' || ((n-1)%180),
 'mock-session-' || ((n-1)%240),
 (ARRAY['PAGE_VIEW','UNIQUE_VIEW','OUTBOUND_CLICK','READ_COMPLETE','SCROLL_DEPTH'])[((n-1)%5)+1],
 2 + (n%180), n%101, TRUE, NOW() - (n%240 || ' minutes')::interval
FROM generate_series(1, 520) AS n;
