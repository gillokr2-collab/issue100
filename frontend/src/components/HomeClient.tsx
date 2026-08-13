"use client";

import { useEffect, useRef, useState, useTransition } from "react";
import Link from "next/link";
import { api } from "@/lib/api";
import type { Issue, PopularVideosResponse, RankingResponse, RankingType, Trend } from "@/types";
import { Header } from "./Header";
import { IssueCard } from "./IssueCard";
import { AdSlot } from "./AdSlot";
import { VideoFeed } from "./VideoFeed";

const types:{value:RankingType;label:string}[]=[
  {value:"REALTIME",label:"실시간"},
  {value:"RISING",label:"급상승"},
  {value:"TODAY",label:"오늘"},
  {value:"WEEKLY",label:"주간"}
];
const categories=["전체","정치","경제","사회","국제","연예","스포츠","IT·과학","생활","문화","건강"];

export function HomeClient({initial,trends,videos}:{initial:RankingResponse;trends:Trend[];videos:PopularVideosResponse}){
  const [items,setItems]=useState<Issue[]>(initial.items);
  const [type,setType]=useState<RankingType>("REALTIME");
  const [category,setCategory]=useState("전체");
  const [expanded,setExpanded]=useState(false);
  const [total,setTotal]=useState(initial.total);
  const [loadingMore,setLoadingMore]=useState(false);
  const loaderRef=useRef<HTMLDivElement>(null);
  const requestKey=useRef(0);
  const [pending,startTransition]=useTransition();

  function load(nextType:RankingType,nextCategory:string){
    setType(nextType);
    setCategory(nextCategory);
    const key=++requestKey.current;
    startTransition(async()=>{
      const response=await api.rankings(nextType,nextCategory==="\uC804\uCCB4"?undefined:nextCategory,0,20);
      if(key===requestKey.current){setItems(response.items);setTotal(response.total);}
    });
  }

  useEffect(()=>{
    const target=loaderRef.current;
    if(!target||items.length>=total)return;
    const observer=new IntersectionObserver(async entries=>{
      if(!entries[0].isIntersecting||loadingMore)return;
      setLoadingMore(true);
      try{
        const response=await api.rankings(type,category==="\uC804\uCCB4"?undefined:category,items.length,20);
        setTotal(response.total);
        setItems(current=>{
          const known=new Set(current.map(item=>item.id));
          return [...current,...response.items.filter(item=>!known.has(item.id))];
        });
      }finally{setLoadingMore(false);}
    },{rootMargin:"600px"});
    observer.observe(target);
    return()=>observer.disconnect();
  },[category,items.length,loadingMore,total,type]);

  return <main className="shell">
    <Header/>
    {items[0]&&<Link className="breaking" href={`/issues/${items[0].id}`}>
      <b>속보</b>
      <span>{items[0].title}</span>
    </Link>}
    <section className="trends">
      <header><i/><h2>실시간 인기 검색어</h2><small>5분 전 갱신</small></header>
      <div className="trend-grid">
        {trends.slice(0,expanded?10:6).map(trend=>
          <Link href={`/search?query=${encodeURIComponent(trend.keyword)}`} key={trend.keyword}>
            <b>{trend.rank}</b>
            <span>{trend.keyword}</span>
            <em className={trend.status.toLowerCase()}>
              {trend.status==="NEW"?"NEW":trend.status==="SAME"?"―":`${trend.status==="UP"?"▲":"▼"}${trend.rankChange}`}
            </em>
          </Link>
        )}
      </div>
      <button className="expand" onClick={()=>setExpanded(value=>!value)}>
        {expanded?"접기 ︿":"10위까지 전체 보기 ﹀"}
      </button>
    </section>
    <AdSlot position="HOME_TOP"/>
    <div className="sticky-tabs">
      <nav className="tabs">
        {types.map(item=><button key={item.value} className={type===item.value?"active":""}
          onClick={()=>load(item.value,category)}>{item.label}</button>)}
      </nav>
      <nav className="chips">
        {categories.map(item=><button key={item} className={category===item?"active":""}
          onClick={()=>load(type,item)}>{item}</button>)}
      </nav>
    </div>
    <section className="ranking-head">
      <h1>{category==="전체"?"실시간 종합 순위":`${category} 순위`}</h1>
      <p>여러 출처를 이슈별로 묶었어요</p>
    </section>
    {pending
      ? <div className="loading-list">{Array.from({length:5},(_,index)=><div className="skeleton card" key={index}/>)}</div>
      : items.length
        ? <section className="issue-list">{items.flatMap((issue,index)=>{
            const cards=[<IssueCard issue={issue} key={`issue-${issue.id}`}/>];
            const video=videos.items[Math.floor(index/3)];
            if((index+1)%3===0&&video)cards.push(<VideoFeed video={video} key={`video-${video.id}`}/>);
            return cards;
          })}</section>
        : <section className="empty"><b>표시할 이슈가 없습니다</b><p>다른 카테고리를 선택해 보세요.</p></section>}
    <div ref={loaderRef} aria-live="polite">{loadingMore&&<div className="skeleton card"/>}</div>
    <AdSlot position="HOME_RANKING_MIDDLE"/>
    <nav className="bottom">
      <Link className="active" href="/">⌂<span>홈</span></Link>
      <a href="#category">▦<span>카테고리</span></a>
      <Link href="/search">⌕<span>검색</span></Link>
      <button onClick={()=>load("RISING","전체")}>↗<span>급상승</span></button>
      <button>⚙<span>설정</span></button>
    </nav>
  </main>;
}
