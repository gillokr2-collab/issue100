import { Suspense } from "react";
import { SearchClient } from "@/components/SearchClient";
export default function SearchPage(){return <Suspense fallback={<main className="shell"><div className="skeleton hero"/></main>}><SearchClient/></Suspense>;}
