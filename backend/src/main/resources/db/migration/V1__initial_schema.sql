CREATE TABLE publisher (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    domain VARCHAR(255) NOT NULL UNIQUE,
    logo_url VARCHAR(500),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE issue (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(500) NOT NULL,
    slug VARCHAR(500) NOT NULL UNIQUE,
    summary TEXT NOT NULL,
    primary_category VARCHAR(40) NOT NULL,
    interest_score NUMERIC(5,1) NOT NULL DEFAULT 0,
    current_rank INTEGER,
    previous_rank INTEGER,
    rank_change INTEGER NOT NULL DEFAULT 0,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    visibility VARCHAR(30) NOT NULL DEFAULT 'PUBLIC',
    review_status VARCHAR(30) NOT NULL DEFAULT 'AUTO_PUBLISHED',
    first_reported_at TIMESTAMPTZ NOT NULL,
    last_updated_at TIMESTAMPTZ NOT NULL,
    total_article_count INTEGER NOT NULL DEFAULT 0,
    publisher_count INTEGER NOT NULL DEFAULT 0,
    total_page_views BIGINT NOT NULL DEFAULT 0,
    total_unique_views BIGINT NOT NULL DEFAULT 0,
    total_outbound_clicks BIGINT NOT NULL DEFAULT 0,
    total_search_count BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_issue_status ON issue(status);
CREATE INDEX idx_issue_category_score ON issue(primary_category, interest_score DESC);
CREATE INDEX idx_issue_updated_at ON issue(last_updated_at DESC);

CREATE TABLE article (
    id BIGSERIAL PRIMARY KEY,
    external_article_id VARCHAR(255) NOT NULL UNIQUE,
    title VARCHAR(500) NOT NULL,
    description TEXT,
    original_url VARCHAR(1000) NOT NULL UNIQUE,
    publisher_id BIGINT NOT NULL REFERENCES publisher(id),
    author VARCHAR(100),
    published_at TIMESTAMPTZ NOT NULL,
    collected_at TIMESTAMPTZ NOT NULL,
    image_url VARCHAR(1000),
    source_type VARCHAR(30) NOT NULL,
    official_source BOOLEAN NOT NULL DEFAULT FALSE,
    primary_category VARCHAR(40) NOT NULL,
    content_hash VARCHAR(64) NOT NULL UNIQUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_article_published_at ON article(published_at DESC);
CREATE INDEX idx_article_publisher_id ON article(publisher_id);

CREATE TABLE issue_article (
    id BIGSERIAL PRIMARY KEY,
    issue_id BIGINT NOT NULL REFERENCES issue(id),
    article_id BIGINT NOT NULL REFERENCES article(id),
    similarity_score NUMERIC(5,4) NOT NULL,
    representative BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_issue_article UNIQUE(issue_id, article_id)
);
CREATE INDEX idx_issue_article_issue ON issue_article(issue_id);
CREATE INDEX idx_issue_article_article ON issue_article(article_id);

CREATE TABLE issue_timeline (
    id BIGSERIAL PRIMARY KEY,
    issue_id BIGINT NOT NULL REFERENCES issue(id),
    event_time TIMESTAMPTZ NOT NULL,
    title VARCHAR(300) NOT NULL,
    description TEXT,
    source_article_id BIGINT REFERENCES article(id),
    display_order INTEGER NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE issue_view_event (
    id BIGSERIAL PRIMARY KEY,
    issue_id BIGINT NOT NULL REFERENCES issue(id),
    visitor_id VARCHAR(64) NOT NULL,
    session_id VARCHAR(64) NOT NULL,
    event_type VARCHAR(30) NOT NULL,
    duration_seconds INTEGER,
    scroll_depth INTEGER,
    valid_event BOOLEAN NOT NULL DEFAULT TRUE,
    viewed_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_view_issue_time ON issue_view_event(issue_id, viewed_at DESC);
CREATE INDEX idx_view_visitor_issue_time ON issue_view_event(visitor_id, issue_id, viewed_at DESC);

CREATE TABLE search_event (
    id BIGSERIAL PRIMARY KEY,
    keyword VARCHAR(300) NOT NULL,
    normalized_keyword VARCHAR(300) NOT NULL,
    canonical_keyword VARCHAR(300) NOT NULL,
    visitor_id VARCHAR(64) NOT NULL,
    session_id VARCHAR(64) NOT NULL,
    result_count INTEGER NOT NULL,
    result_clicked BOOLEAN NOT NULL DEFAULT FALSE,
    valid_search BOOLEAN NOT NULL DEFAULT TRUE,
    device_type VARCHAR(30),
    referrer VARCHAR(500),
    searched_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_search_keyword_time ON search_event(normalized_keyword, searched_at DESC);
CREATE INDEX idx_search_visitor_keyword_time ON search_event(visitor_id, normalized_keyword, searched_at DESC);

CREATE TABLE search_ranking_snapshot (
    id BIGSERIAL PRIMARY KEY,
    normalized_keyword VARCHAR(300) NOT NULL,
    display_keyword VARCHAR(300) NOT NULL,
    ranking_type VARCHAR(30) NOT NULL,
    rank INTEGER NOT NULL,
    previous_rank INTEGER,
    rank_status VARCHAR(20) NOT NULL,
    rank_change INTEGER NOT NULL DEFAULT 0,
    score NUMERIC(10,3) NOT NULL,
    search_count INTEGER NOT NULL,
    unique_searcher_count INTEGER NOT NULL,
    click_through_rate NUMERIC(6,4) NOT NULL,
    calculated_at TIMESTAMPTZ NOT NULL
);
CREATE INDEX idx_search_rank_time ON search_ranking_snapshot(ranking_type, calculated_at DESC, rank);

CREATE TABLE search_blocked_keyword (
    id BIGSERIAL PRIMARY KEY,
    keyword VARCHAR(300) NOT NULL UNIQUE,
    reason VARCHAR(500) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
