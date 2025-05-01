import { useState } from "react"

import { cn } from "@/lib/utils"
import { Link } from "react-router-dom"

const categories = [
  { id: "all", name: "전체" },
  { id: "musical", name: "뮤지컬" },
  { id: "concert", name: "콘서트" },
  { id: "exhibition", name: "전시/행사" },
  { id: "classic", name: "클래식/무용" },
  { id: "theater", name: "연극" },
]

const rankingData = [
  {
    id: 1,
    title: "라이온 킹",
    venue: "오픈런드 극장",
    category: "뮤지컬",
    imageUrl:
      "/placeholder.svg?height=400&width=300&query=the lion king musical poster yellow background",
  },
  {
    id: 2,
    title: "아델 마이 러브",
    venue: "시티즌 아레나홀",
    category: "콘서트",
    imageUrl:
      "/placeholder.svg?height=400&width=300&query=adele concert poster",
  },
  {
    id: 3,
    title: "로미오와 줄리엣",
    venue: "로얄 오페라 하우스",
    category: "뮤지컬",
    imageUrl:
      "/placeholder.svg?height=400&width=300&query=romeo and juliet musical poster",
  },
  {
    id: 4,
    title: "해밀턴",
    venue: "브로드웨이 극장",
    category: "뮤지컬",
    imageUrl:
      "/placeholder.svg?height=400&width=300&query=hamilton musical poster",
  },
  {
    id: 5,
    title: "BTS 월드투어",
    venue: "올림픽 주경기장",
    category: "콘서트",
    imageUrl: "/placeholder.svg?height=400&width=300&query=bts concert poster",
  },
  {
    id: 6,
    title: "반 고흐 전시회",
    venue: "국립현대미술관",
    category: "전시/행사",
    imageUrl:
      "/placeholder.svg?height=400&width=300&query=van gogh exhibition poster",
  },
]

export default function GenreRanking() {
  const [activeCategory, setActiveCategory] = useState("all")

  const filteredRankings =
    activeCategory === "all"
      ? rankingData
      : rankingData.filter((item) =>
          item.category.toLowerCase().includes(activeCategory),
        )

  return (
    <div>
      {/* 카테고리 탭 */}
      <div className="flex justify-center mb-8">
        <div className="inline-flex bg-gray-100 rounded-full p-1">
          {categories.map((category) => (
            <button
              key={category.id}
              className={cn(
                "px-4 py-2 text-sm font-medium rounded-full transition-colors",
                activeCategory === category.id
                  ? "bg-white shadow"
                  : "text-gray-600 hover:text-gray-900",
              )}
              onClick={() => setActiveCategory(category.id)}
            >
              {category.name}
            </button>
          ))}
        </div>
      </div>

      {/* 랭킹 그리드 */}
      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-3 gap-6">
        {filteredRankings.slice(0, 3).map((item) => (
          <Link key={item.id} to={`/show/${item.id}`} className="group">
            <div className="bg-white rounded-lg overflow-hidden shadow-md transition-shadow hover:shadow-lg">
              <div className="relative h-80 overflow-hidden">
                <img
                  src={item.imageUrl || "/placeholder.svg"}
                  alt={item.title}
                  className="object-cover group-hover:scale-105 transition-transform duration-300"
                />
              </div>
              <div className="p-4">
                <p className="text-sm text-gray-500 mb-1">{item.category}</p>
                <h3 className="font-bold text-lg mb-1 group-hover:text-blue-600 transition-colors">
                  {item.title}
                </h3>
                <p className="text-gray-600">{item.venue}</p>
              </div>
            </div>
          </Link>
        ))}
      </div>
    </div>
  )
}
