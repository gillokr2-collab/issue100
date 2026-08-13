import type { Article, IssueDetail, PopularVideosResponse, RankingResponse, RankingType, SearchResponse, Timeline, Trend } from "@/types";

function apiBase(){
  return typeof window==="undefined"
    ? (process.env.INTERNAL_API_BASE_URL??process.env.NEXT_PUBLIC_API_BASE_URL??"http://localhost:8080")
    : (process.env.NEXT_PUBLIC_API_BASE_URL??"http://localhost:8080");
}

async function request<T>(path:string,init?:RequestInit):Promise<T>{
  const response=await fetch(`${apiBase()}${path}`,{
    ...init,
    headers:{"Content-Type":"application/json",...init?.headers},
    cache:"no-store"
  });
  if(!response.ok) throw new Error(`API ${response.status}`);
  return await response.json() as T;
}

export const api={
  rankings:(type:RankingType,category?:string,offset=0,limit=20)=>request<RankingResponse>(
    `/api/v1/issues/rankings?type=${type}&offset=${offset}&limit=${limit}${category?`&category=${encodeURIComponent(category)}`:""}`
  ),
  trends:()=>request<{items:Trend[]}>(`/api/v1/search/rankings/realtime`),
  detail:(id:number)=>request<IssueDetail>(`/api/v1/issues/${id}`),
  articles:(id:number)=>request<Article[]>(`/api/v1/issues/${id}/articles`),
  timeline:(id:number)=>request<Timeline[]>(`/api/v1/issues/${id}/timeline`),
  search:(query:string)=>request<SearchResponse>(`/api/v1/search?query=${encodeURIComponent(query)}`),
  popularVideos:()=>request<PopularVideosResponse>(`/api/v1/videos/popular`),
  post:(path:string,body:unknown)=>request(path,{method:"POST",body:JSON.stringify(body)})
};
