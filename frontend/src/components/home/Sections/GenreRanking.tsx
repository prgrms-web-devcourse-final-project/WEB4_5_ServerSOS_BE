import { useState } from "react"

import { cn } from "@/lib/utils"
import { Link } from "react-router-dom"
import { usePopularPost } from "@/hooks/usePopularPost"
import type { GetPopularPostsTypeEnum } from "@/api/__generated__"
import { CATEGORIES } from "@/components/category/constants"

export default function GenreRanking() {
  const [activeCategory, setActiveCategory] =
    useState<GetPopularPostsTypeEnum>("MUSICAL")

  const { popularPosts, isLoading } = usePopularPost({
    type: activeCategory,
  })

  if (!isLoading && (!popularPosts || popularPosts?.length === 0)) {
    return <div>장르 랭킹이 없습니다.</div>
  }

  return (
    <div>
      {/* 카테고리 탭 */}
      <div className="flex justify-center mb-8">
        <div className="inline-flex bg-gray-100 rounded-full p-1">
          {CATEGORIES.map((category) => (
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
        {popularPosts?.slice(0, 3).map((item) => (
          <Link key={item.id} to={`/show/${item.id}`} className="group">
            <div className="bg-white rounded-lg overflow-hidden shadow-md transition-shadow hover:shadow-lg">
              <div className="relative h-80 overflow-hidden">
                <img
                  src={item.poster || "/placeholder.svg"}
                  alt={item.title}
                  className="object-cover group-hover:scale-105 transition-transform duration-300 w-full h-full"
                />
              </div>
              <div className="p-4">
                <p className="text-sm text-gray-500 mb-1">
                  {
                    CATEGORIES.find(
                      (category) => category.id === activeCategory,
                    )?.name
                  }
                </p>
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
