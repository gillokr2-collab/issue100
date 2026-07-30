import { api } from "@/lib/api";
import { HomeClient } from "@/components/HomeClient";
export default async function Home(){const [rankings,trends]=await Promise.all([api.rankings("REALTIME"),api.trends()]);return <HomeClient initial={rankings} trends={trends.items}/>;}
