import { Link } from "react-router-dom"

const upcomingShows = [
  {
    id: 1,
    title: "킹덤 뮤지컬",
    date: "2025.09.20",
    category: "뮤지컬",
    imageUrl: "/placeholder.svg?height=100&width=100&query=ticket icon",
  },
  {
    id: 2,
    title: "스타보이 콘서트",
    date: "2025.10.05",
    category: "콘서트",
    imageUrl: "/placeholder.svg?height=100&width=100&query=music festival icon",
  },
  {
    id: 3,
    title: "위키드",
    date: "2025.11.15",
    category: "뮤지컬",
    imageUrl: "/placeholder.svg?height=100&width=100&query=theater mask icon",
  },
  {
    id: 4,
    title: "팬텀 오브 디 오페라",
    date: "2025.12.01",
    category: "뮤지컬",
    imageUrl: "/placeholder.svg?height=100&width=100&query=phantom mask icon",
  },
]

export default function UpcomingShows() {
  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-4 gap-8">
      {upcomingShows.map((show) => (
        <Link key={show.id} to={`/upcoming/${show.id}`} className="group">
          <div className="flex flex-col items-center text-center">
            <div className="w-24 h-24 bg-white rounded-full flex items-center justify-center mb-4 shadow-md group-hover:shadow-lg transition-shadow">
              <img
                src={show.imageUrl || "/placeholder.svg"}
                alt={show.title}
                width={60}
                height={60}
                className="object-contain"
              />
            </div>
            <p className="text-sm text-gray-500 mb-1">{show.category}</p>
            <h3 className="font-medium mb-2 group-hover:text-blue-600 transition-colors">
              {show.title}
            </h3>
            <p className="text-lg font-bold">{show.date}</p>
          </div>
        </Link>
      ))}
    </div>
  )
}
