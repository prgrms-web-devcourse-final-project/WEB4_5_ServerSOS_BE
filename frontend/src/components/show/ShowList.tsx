import { useState, useEffect } from "react"

import { format } from "date-fns"
import { ko } from "date-fns/locale"
import { ChevronLeft, ChevronRight } from "lucide-react"
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select"
import { Link } from "react-router-dom"

interface Show {
  id: number
  title: string
  venue: string
  startDate: string
  endDate: string
  poster: string
}

interface ShowListResponse {
  data: {
    items: Show[]
    page: number
    size: number
    totalPages: number
    totalElements: number
  }
}

export default function ShowList({ genre }: { genre: string }) {
  const [shows, setShows] = useState<Show[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [currentPage, setCurrentPage] = useState(1)
  const [totalPages, setTotalPages] = useState(1)
  const [sortOption, setSortOption] = useState("latest")

  useEffect(() => {
    const fetchShows = async () => {
      setLoading(true)
      try {
        // 실제 API 호출 대신 목업 데이터 사용
        // 실제 구현 시에는 아래 주석을 해제하고 목업 데이터 부분을 제거하세요
        // const response = await fetch(`/api/shows?genre=${genre}&page=${currentPage}&sort=${sortOption}`);
        // const data: ShowListResponse = await response.json();

        // 목업 데이터
        const mockData: ShowListResponse = {
          data: {
            items: Array.from({ length: 8 }, (_, i) => ({
              id: i + 1,
              title: `${genre === "all" ? "인기" : genre} 공연 ${i + 1}`,
              venue: `${["코엑스 아티움", "세종문화회관", "블루스퀘어", "예술의전당"][i % 4]}`,
              startDate: "2025-04-29T14:30:45",
              endDate: "2025-05-30T14:30:45",
              poster: `/placeholder.svg?height=400&width=280&query=${genre} performance poster ${i + 1}`,
            })),
            page: currentPage,
            size: 8,
            totalPages: 5,
            totalElements: 40,
          },
        }

        // 정렬 옵션에 따라 데이터 정렬
        const sortedItems = [...mockData.data.items]
        if (sortOption === "latest") {
          sortedItems.sort(
            (a, b) =>
              new Date(b.startDate).getTime() - new Date(a.startDate).getTime(),
          )
        } else if (sortOption === "oldest") {
          sortedItems.sort(
            (a, b) =>
              new Date(a.startDate).getTime() - new Date(b.startDate).getTime(),
          )
        } else if (sortOption === "title") {
          sortedItems.sort((a, b) => a.title.localeCompare(b.title))
        }

        setShows(sortedItems)
        setTotalPages(mockData.data.totalPages)
        setError(null)
      } catch (err) {
        setError("공연 정보를 불러오는 데 실패했습니다.")
        console.error(err)
      } finally {
        setLoading(false)
      }
    }

    fetchShows()
  }, [genre, currentPage, sortOption])

  const handlePageChange = (page: number) => {
    if (page < 1 || page > totalPages) return
    setCurrentPage(page)
    window.scrollTo({ top: 0, behavior: "smooth" })
  }

  const formatDateRange = (startDate: string, endDate: string) => {
    const start = format(new Date(startDate), "yyyy.MM.dd", { locale: ko })
    const end = format(new Date(endDate), "yyyy.MM.dd", { locale: ko })
    return `${start} - ${end}`
  }

  if (error) {
    return <div className="text-center py-10 text-red-500">{error}</div>
  }

  return (
    <div>
      {/* 정렬 옵션 */}
      <div className="flex justify-end mb-6">
        <Select value={sortOption} onValueChange={setSortOption}>
          <SelectTrigger className="w-[180px]">
            <SelectValue placeholder="정렬 옵션" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="latest">최신순</SelectItem>
            <SelectItem value="oldest">오래된순</SelectItem>
            <SelectItem value="title">제목순</SelectItem>
          </SelectContent>
        </Select>
      </div>

      {/* 공연 목록 그리드 */}
      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
        {shows.map((show) => (
          <Link
            key={show.id}
            to={`/show/${show.id}`}
            className="bg-white rounded-lg overflow-hidden shadow-md hover:shadow-lg transition-shadow"
          >
            <div className="relative h-[380px]">
              <div className="absolute inset-0 bg-gray-200 animate-pulse"></div>
              <img
                src={show.poster || "/placeholder.svg"}
                alt={show.title}
                className="object-cover z-10"
              />
            </div>
            <div className="p-4">
              <h3 className="font-bold text-lg mb-1 line-clamp-1">
                {show.title}
              </h3>
              <p className="text-gray-600 mb-2 text-sm">{show.venue}</p>
              <p className="text-gray-500 text-sm">
                {formatDateRange(show.startDate, show.endDate)}
              </p>
            </div>
          </Link>
        ))}
      </div>

      {/* 페이지네이션 */}
      <div className="flex justify-center mt-10">
        <nav className="flex items-center space-x-2">
          <button
            onClick={() => handlePageChange(currentPage - 1)}
            disabled={currentPage === 1}
            className="p-2 rounded-md border enabled:hover:bg-gray-100 disabled:opacity-50"
            aria-label="이전 페이지"
          >
            <ChevronLeft className="h-5 w-5" />
          </button>

          {Array.from({ length: totalPages }, (_, i) => i + 1).map((page) => (
            <button
              key={page}
              onClick={() => handlePageChange(page)}
              className={`px-4 py-2 rounded-md ${
                currentPage === page
                  ? "bg-blue-600 text-white"
                  : "border hover:bg-gray-100"
              }`}
            >
              {page}
            </button>
          ))}

          <button
            onClick={() => handlePageChange(currentPage + 1)}
            disabled={currentPage === totalPages}
            className="p-2 rounded-md border enabled:hover:bg-gray-100 disabled:opacity-50"
            aria-label="다음 페이지"
          >
            <ChevronRight className="h-5 w-5" />
          </button>
        </nav>
      </div>
    </div>
  )
}
