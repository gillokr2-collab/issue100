import type { PopularVideo } from "@/types";

function relativeTime(value:string){
  const minutes=Math.max(1,Math.round((Date.now()-new Date(value).getTime())/60000));
  return new Intl.RelativeTimeFormat("ko",{numeric:"auto"}).format(-minutes,"minute");
}

export function VideoFeed({video}:{video:PopularVideo}){
  return <a className="issue-card video-feed-card" href={video.videoUrl} target="_blank" rel="noopener noreferrer">
    <div className="issue-body">
      <div className="video-image"><img className="issue-thumb" src={video.thumbnailUrl} alt="" loading="lazy"/><i>▶</i></div>
      <div className="eyebrow"><span>YouTube</span><em>{"\uC778\uAE30 \uC601\uC0C1"}</em></div>
      <h2>{video.title}</h2>
      <p>{`${video.channelTitle}\uC5D0\uC11C \uACF5\uAC1C\uD55C \uC778\uAE30 \uC601\uC0C1\uC785\uB2C8\uB2E4. \uC6D0\uBCF8 \uC601\uC0C1\uC740 YouTube\uC5D0\uC11C \uD655\uC778\uD558\uC138\uC694.`}</p>
      <footer><strong>YouTube</strong><span>{video.channelTitle}</span><span>{"\uC870\uD68C "}{new Intl.NumberFormat("ko",{notation:"compact"}).format(video.viewCount)}{"\uD68C"}</span><time>{relativeTime(video.publishedAt)}</time></footer>
    </div>
  </a>;
}