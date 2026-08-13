import type { Issue } from "@/types";
import { api } from "@/lib/api";
import { sessionId, visitorId } from "@/lib/visitor";


export function IssueCard({issue}:{issue:Issue}){
  async function outbound(event:React.MouseEvent<HTMLAnchorElement>){
    if(!issue.originalUrl)return;
    event.preventDefault();
    event.stopPropagation();
    try{
      await api.post(`/api/v1/articles/${issue.id}/outbound-click`,{
        visitorId:visitorId(),sessionId:sessionId()
      });
    }finally{
      window.location.assign(issue.originalUrl);
    }
  }
  return <a className={`issue-card ${issue.imageUrl?"with-image":""}`}
    href={issue.originalUrl??`/issues/${issue.id}`} onClick={outbound}>    <div className="issue-body">
      {issue.imageUrl&&<img className="issue-thumb" src={issue.imageUrl} alt="" loading="lazy"/>}
      <div className="eyebrow"><span>{issue.category}</span>{issue.rankStatus==="NEW"&&<em>NEW</em>}{issue.articleVelocity>140&&<em className="hot">HOT</em>}</div>
      <h2>{issue.title}</h2>
      <p>{issue.aiSummary}</p>
      <footer><strong>{issue.sourceType==="COMMUNITY"?"\uCEE4\uBBA4\uB2C8\uD2F0":"\uB274\uC2A4"}</strong><span>{issue.publisher??issue.tags.at(-1)??"\uCD9C\uCC98 \uD655\uC778"}</span><time>{new Intl.RelativeTimeFormat("ko",{numeric:"auto"}).format(-Math.max(1,Math.round((Date.now()-new Date(issue.updatedAt).getTime())/60000)),"minute")}</time></footer>
    </div>
  </a>;
}
