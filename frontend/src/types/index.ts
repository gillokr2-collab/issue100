export type RankingType = "REALTIME" | "RISING" | "TODAY" | "WEEKLY";
export type RankStatus = "UP" | "DOWN" | "SAME" | "NEW";
export interface Issue {
  id: number; rank: number; previousRank: number; rankStatus: RankStatus; category: string;
  title: string; aiSummary: string; interestScore: number; articleCount: number;
  publisherCount: number; pageViews: number; uniqueViews: number; outboundClicks: number;
  searchCount: number; articleVelocity: number; updatedAt: string; imageUrl?: string;
  originalUrl?: string; publisher?: string; sourceType?: "NEWS" | "COMMUNITY"; tags: string[];
}
export interface RankingResponse { items: Issue[]; total: number; type: RankingType; category?: string; calculatedAt: string; }
export interface Article { id: number; issueId: number; publisher: string; title: string; originalUrl: string; imageUrl?: string; publishedAt: string; officialSource: boolean; }
export interface Timeline { id: number; issueId: number; eventTime: string; title: string; description: string; displayOrder: number; }
export interface Entity { type: string; name: string; }
export interface IssueDetail { issue: Issue; keyFacts: string[]; entities: Entity[]; relatedIssueIds: number[]; }
export interface Trend { rank: number; previousRank: number; status: RankStatus; rankChange: number; keyword: string; searchCount: number; }
export interface SearchResponse { query: string; normalizedQuery: string; issues: Issue[]; articles: Article[]; total: number; }
export interface PopularVideo { id: string; title: string; channelTitle: string; thumbnailUrl: string; videoUrl: string; publishedAt: string; viewCount: number; }
export interface PopularVideosResponse { items: PopularVideo[]; configured: boolean; region: string; }
