import { api } from "@/lib/api";
import { HomeClient } from "@/components/HomeClient";
export default async function Home(){const [rankings,trends,videos]=await Promise.all([api.rankings("REALTIME"),api.trends(),api.popularVideos()]);return <HomeClient initial={rankings} trends={trends.items} videos={videos}/>;}
