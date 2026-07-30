import type { Metadata } from "next";
import "./globals.css";
export const metadata:Metadata={title:"이슈100 — 지금, 한국이 보는 뉴스",description:"여러 언론사의 기사를 사건 단위로 묶어 보여주는 실시간 뉴스 관심도 순위"};
export default function RootLayout({children}:{children:React.ReactNode}){return <html lang="ko"><body>{children}</body></html>;}
