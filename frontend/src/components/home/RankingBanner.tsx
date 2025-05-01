import { useState, useEffect, useCallback } from "react"

import { ChevronLeft, ChevronRight, Image } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Link } from "react-router-dom"

const bannerData = {
  title: "뮤지컬 원스",
  subtitle: "음악으로 기억될 사랑의 순간",
  venue: "coex 신한카드 artium",
  period: "2025.2.19 - 2025.5.31",
  imageUrl: "/musical-banner.png",
}

const smallPosters = [
  "/musical-poster-1.png",
  "/musical-poster-2.png",
  "/musical-poster-3.png",
  "/musical-poster-4.png",
  "/placeholder.svg?height=80&width=80&query=musical poster 5",
  "/placeholder.svg?height=80&width=80&query=musical poster 6",
  "/placeholder.svg?height=80&width=80&query=musical poster 7",
  "/placeholder.svg?height=80&width=80&query=musical poster 8",
  "/placeholder.svg?height=80&width=80&query=musical poster 9",
  "/placeholder.svg?height=80&width=80&query=musical poster 10",
]

export default function RankingBanner() {
  const [currentIndex, setCurrentIndex] = useState(0)

  const nextSlide = useCallback(() => {
    setCurrentIndex((prev) => (prev + 1) % smallPosters.length)
  }, [])

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
        <img
          src={bannerData.imageUrl || "/placeholder.svg"}
          alt={bannerData.title}
          className="object-cover opacity-90"
        />
        <div className="absolute inset-0 bg-gradient-to-r from-black/70 to-transparent" />

        <div className="absolute inset-0 flex items-center">
          <div className="container mx-auto px-4">
            <div className="max-w-lg text-white">
              <h1 className="text-5xl font-bold mb-2">{bannerData.title}</h1>
              <p className="text-xl mb-4">{bannerData.subtitle}</p>
              <p className="text-sm mb-1">{bannerData.venue}</p>
              <p className="text-sm mb-6">{bannerData.period}</p>
              <Link to="/reservation">
                <Button size="lg">예매하기</Button>
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
                <Link key={index} to={`/show/${index}`}>
                  <div className="w-20 h-20 rounded overflow-hidden flex-shrink-0 border border-gray-700 hover:border-white transition-colors">
                    <img
                      src={poster || "/placeholder.svg"}
                      alt={`포스터 ${index + 1}`}
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
