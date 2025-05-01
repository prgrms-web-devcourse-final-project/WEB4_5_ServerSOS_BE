import CategoryNavigation from "@/components/home/CategoryNavigation"
import { PageLayout } from "../layout/PageLayout"
import RankingBanner from "@/components/home/RankingBanner"
import { Sections } from "@/components/home/Sections/Sections"

export const Home = () => {
  return (
    <PageLayout>
      <CategoryNavigation />
      <RankingBanner />
      <Sections />
    </PageLayout>
  )
}
