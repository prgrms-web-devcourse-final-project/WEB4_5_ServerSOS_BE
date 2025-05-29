import { useOpenPost } from "@/hooks/useOpenPost"
import { formatDate } from "date-fns"
import { Link } from "react-router-dom"

export default function UpcomingShows() {
  const { openPost } = useOpenPost()

  if (!openPost || openPost?.length === 0) {
    return <div>오픈 예정 공연이 없습니다.</div>
  }

  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-4 gap-8">
      {openPost.map((show) => (
        <Link key={show.id} to={`/show/${show.id}`} className="group">
          <div className="flex flex-col items-center text-center">
            <div className="w-24 h-24 bg-white rounded-full flex items-center justify-center mb-4 shadow-md group-hover:shadow-lg transition-shadow overflow-hidden">
              <img
                src={show.poster || "/placeholder.svg"}
                alt={show.title}
                width={60}
                height={60}
                className="object-cover w-full h-full"
              />
            </div>

            <h3 className="font-medium mb-2 group-hover:text-blue-600 transition-colors">
              {show.title}
            </h3>
            <p className="text-lg font-bold">
              {show.startDate
                ? formatDate(show.startDate, "yyyy.MM.dd")
                : "오픈 예정"}
            </p>
          </div>
        </Link>
      ))}
    </div>
  )
}
