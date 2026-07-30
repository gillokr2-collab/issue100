import { api } from "@/lib/api";
import { IssueDetailClient } from "@/components/IssueDetailClient";
export default async function IssuePage({params}:{params:Promise<{id:string}>}){const {id}=await params;const issueId=Number(id);const [detail,articles,timeline]=await Promise.all([api.detail(issueId),api.articles(issueId),api.timeline(issueId)]);return <IssueDetailClient detail={detail} articles={articles} timeline={timeline}/>;}
