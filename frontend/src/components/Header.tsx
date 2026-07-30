"use client";
import Link from "next/link";
import { FormEvent, useState } from "react";
import { useRouter } from "next/navigation";
export function Header(){const [query,setQuery]=useState("");const router=useRouter();function submit(e:FormEvent){e.preventDefault();if(query.trim())router.push(`/search?query=${encodeURIComponent(query.trim())}`);}return <><header className="header"><Link href="/" className="logo"><b>100</b>이슈100</Link><span className="live"><i/>LIVE<br/><small>방금 갱신</small></span><Link href="/search" className="icon-btn" aria-label="검색">⌕</Link></header><form className="searchbar" onSubmit={submit}><span>⌕</span><input value={query} onChange={e=>setQuery(e.target.value)} placeholder="이슈, 기사, 인물, 기업 검색" aria-label="검색어"/><button>검색</button></form></>;}
