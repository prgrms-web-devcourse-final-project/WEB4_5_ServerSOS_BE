import { cn } from "@/lib/utils"
import { Link, useParams } from "react-router-dom"
import { CATEGORIES } from "./constants"

export default function CategoryNavigation() {
  const { genre } = useParams()
  const activeCategory = genre || "all"

  return (
    <nav className="border-b sticky top-0 bg-white z-10">
      <div className="container mx-auto px-4">
        <ul className="flex overflow-x-auto hide-scrollbar py-3 gap-6">
          {CATEGORIES.map((category) => (
            <li key={category.id}>
              <Link
                to={`/category/${category.id}`}
                className={cn(
                  "whitespace-nowrap text-sm font-medium transition-colors",
                  activeCategory === category.id
                    ? "text-blue-600 border-b-2 border-blue-600 pb-3"
                    : "text-gray-600 hover:text-gray-900",
                )}
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
