import { useState, useEffect, useCallback } from "react"

import { ChevronLeft, ChevronRight } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Link } from "react-router-dom"
import { usePopularPost } from "@/hooks/usePopularPost"
import { getDurationStr } from "@/lib/date"

export default function RankingBanner() {
  const [currentIndex, setCurrentIndex] = useState(0)

  const { popularPosts } = usePopularPost({})

  const [bannerData, ...smallPosters] =
    popularPosts && popularPosts.length > 0 ? popularPosts : [null, null]

  console.log("#popularPosts", popularPosts, bannerData, smallPosters)

  const nextSlide = useCallback(() => {
    setCurrentIndex((prev) => (prev + 1) % smallPosters.length)
  }, [smallPosters.length])

  const prevSlide = () => {
    setCurrentIndex(
      (prev) => (prev - 1 + smallPosters.length) % smallPosters.length,
    )
  }

  useEffect(() => {
    const interval = setInterval(nextSlide, 5000)
    return () => clearInterval(interval)
  }, [nextSlide])

  return (
    <div className="relative">
      {/* 메인 배너 */}
      <div className="relative w-full h-[500px] bg-black">
        <div className="container mx-auto px-4 h-full relative">
          <img
            src={bannerData?.poster ? bannerData?.poster : "/placeholder.svg"}
            alt={bannerData?.title}
            className="object-cover opacity-90 h-full"
          />
          <div className="absolute inset-0 bg-gradient-to-r from-black/70 to-transparent" />
        </div>

        <div className="absolute inset-0 flex items-center">
          <div className="container mx-auto px-4">
            <div className="max-w-lg text-white">
              <h1 className="text-5xl font-bold mb-2">{bannerData?.title}</h1>
              <p className="text-xl mb-4">{bannerData?.views}</p>
              <p className="text-sm mb-1">{bannerData?.venue}</p>
              <p className="text-sm mb-6">
                {getDurationStr(bannerData?.startDate, bannerData?.endDate)}
              </p>
              <Link to={`/show/${bannerData?.id}`}>
                <Button size="lg" variant="secondary">
                  예매하기
                </Button>
              </Link>
            </div>
          </div>
        </div>
      </div>

      {/* 작은 포스터 슬라이더 */}
      <div className="absolute bottom-0 left-0 right-0 bg-black/80 py-4">
        <div className="container mx-auto px-4 relative">
          <button
            onClick={prevSlide}
            className="absolute left-0 top-1/2 -translate-y-1/2 z-10 bg-black/50 rounded-full p-1 text-white"
            aria-label="이전 슬라이드"
          >
            <ChevronLeft size={20} />
          </button>

          <div className="overflow-hidden mx-8">
            <div
              className="flex gap-4 transition-transform duration-300 ease-in-out"
              style={{ transform: `translateX(-${currentIndex * 88}px)` }}
            >
              {smallPosters.map((poster, index) => (
                <Link key={index} to={`/show/${poster?.id}`}>
                  <div className="w-20 h-20 rounded overflow-hidden flex-shrink-0 border border-gray-700 hover:border-white transition-colors">
                    <img
                      src={poster?.poster || "/placeholder.svg"}
                      alt={`포스터 ${poster?.title}`}
                      width={80}
                      height={80}
                      className="object-cover"
                    />
                  </div>
                </Link>
              ))}
            </div>
          </div>

          <button
            onClick={nextSlide}
            className="absolute right-0 top-1/2 -translate-y-1/2 z-10 bg-black/50 rounded-full p-1 text-white"
            aria-label="다음 슬라이드"
          >
            <ChevronRight size={20} />
          </button>
        </div>
      </div>
    </div>
  )
}
