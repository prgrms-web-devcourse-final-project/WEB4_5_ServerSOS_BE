import CategoryNavigation from "@/components/category/CategoryNavigation"
import { PageLayout } from "@/layout/PageLayout"
import { Suspense } from "react"
import { useParams } from "react-router-dom"
import ShowList from "@/components/show/ShowList"
import ShowListSkeleton from "@/components/show/ShowListSkeleton"
import { ALL_CATEGORIES } from "@/components/category/constants"

export default function Category() {
  const { genre } = useParams()
  const genreValue = genre || "ALL"

  // 유효하지 않은 장르인 경우 404 페이지 표시
  if (!ALL_CATEGORIES.some((category) => category.id === genreValue)) {
    throw new Error("유효하지않은 장르")
  }

  return (
    <PageLayout>
      {/* 카테고리 헤더 */}
      <CategoryNavigation />

      {/* 공연 목록 */}
      <div className="container mx-auto px-4 mt-8">
        <Suspense fallback={<ShowListSkeleton />}>
          <ShowList genre={genreValue} />
        </Suspense>
      </div>
    </PageLayout>
  )
}
