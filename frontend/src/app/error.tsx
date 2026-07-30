"use client";
export default function ErrorPage({reset}:{reset:()=>void}){return <main className="shell center"><div className="state-icon">!</div><h1>뉴스를 불러오지 못했습니다</h1><p>잠시 후 다시 시도해 주세요.</p><button className="primary" onClick={reset}>다시 시도</button></main>;}
