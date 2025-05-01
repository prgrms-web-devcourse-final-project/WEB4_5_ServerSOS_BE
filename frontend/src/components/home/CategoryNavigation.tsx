import { useState } from "react"
import { cn } from "@/lib/utils"
import { Link } from "react-router-dom"

const categories = [
  { id: "musical", name: "뮤지컬" },
  { id: "concert", name: "콘서트" },
  { id: "sports", name: "스포츠" },
  { id: "exhibition", name: "전시/행사" },
  { id: "classic", name: "클래식/무용" },
  { id: "theater", name: "연극" },
  { id: "family", name: "가족/아동" },
  { id: "leisure", name: "레저/여행" },
  { id: "md", name: "MD샵" },
  { id: "promotion", name: "할인" },
  { id: "membership", name: "멤버십" },
  { id: "venue", name: "지역별" },
]

export default function CategoryNavigation() {
  const [activeCategory, setActiveCategory] = useState("musical")

  return (
    <nav className="border-b sticky top-0 bg-white z-10">
      <div className="container mx-auto px-4">
        <ul className="flex overflow-x-auto hide-scrollbar py-3 gap-6">
          {categories.map((category) => (
            <li key={category.id}>
              <Link
                to={"/"}
                className={cn(
                  "whitespace-nowrap text-sm font-medium transition-colors",
                  activeCategory === category.id
                    ? "text-blue-600 border-b-2 border-blue-600 pb-3"
                    : "text-gray-600 hover:text-gray-900",
                )}
                onClick={(e) => {
                  e.preventDefault()
                  setActiveCategory(category.id)
                }}
              >
                {category.name}
              </Link>
            </li>
          ))}
        </ul>
      </div>
    </nav>
  )
}
