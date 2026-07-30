UPDATE issue
SET title = '프로야구 후반기 순위 경쟁 본격화',
    summary = REPLACE(summary, '손흥민', '프로야구'),
    updated_at = NOW()
WHERE title = '손흥민 새 시즌 거취에 유럽 현지 언론 집중';

UPDATE search_event
SET keyword = '프로야구 순위',
    normalized_keyword = '프로야구 순위',
    canonical_keyword = '프로야구 순위'
WHERE keyword = '손흥민 이적'
   OR normalized_keyword = '손흥민 이적'
   OR canonical_keyword = '손흥민 이적';
