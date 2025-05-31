import { useState, useEffect } from "react";

import { format } from "date-fns";
import { ko } from "date-fns/locale";
import { ChevronLeft, ChevronRight } from "lucide-react";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Link } from "react-router-dom";
import type { CATEGORY_TYPES } from "../category/constants";
import { usePosts } from "@/hooks/usePosts";
import { getDurationStr } from "@/lib/date";
import { GetPostsSortEnum } from "@/api/__generated__";

interface Show {
  id: number;
  title: string;
  venue: string;
  startDate: string;
  endDate: string;
  poster: string;
}

export default function ShowList({ genre }: { genre: CATEGORY_TYPES }) {
  const [currentPage, setCurrentPage] = useState(1);
  const [sortOption, setSortOption] = useState<GetPostsSortEnum>("ID_DESC");
  const [searchInput, setSearchInput] = useState("");
  const [searchKeyword, setSearchKeyword] = useState("");

  useEffect(() => {
    setSearchInput("");
    setSearchKeyword("");
    setCurrentPage(1);
  }, [genre]);

  const { posts, error, isLoading, totalPages } = usePosts({
    sort: sortOption,
    type: genre,
    page: currentPage,
    keyword: searchKeyword,
  });

  const handlePageChange = (page: number) => {
    if (page < 1 || page > totalPages) return;
    setCurrentPage(page);
    window.scrollTo({ top: 0, behavior: "smooth" });
  };

  if (error) {
    return (
      <div className="text-center py-10 text-red-500">{error.message}</div>
    );
  }

  return (
    <div>
      {/* 정렬 옵션 */}
      <div className="flex flex-col md:flex-row justify-between items-center mb-6 gap-4">
        <div className="flex w-full md:w-1/3 gap-2">
          <input
            type="text"
            value={searchInput}
            onChange={(e) => setSearchInput(e.target.value)}
            onKeyDown={(e) => {
              if (e.key === "Enter") {
                setSearchKeyword(searchInput);
                setCurrentPage(1);
              }
            }}
            placeholder="공연명을 입력하세요"
            className="flex-grow border border-gray-300 rounded-md px-4 py-2 focus:outline-none focus:ring-2"
          />
          <button
            onClick={() => {
              setSearchKeyword(searchInput);
              setCurrentPage(1);
            }}
            className="bg-black text-white px-4 py-2 rounded-md transition"
          >
            검색
          </button>
        </div>
        <Select
          value={sortOption}
          onValueChange={(value) => {
            setSortOption(value as GetPostsSortEnum);
          }}
        >
          <SelectTrigger className="w-[180px]">
            <SelectValue placeholder="정렬 옵션" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="ID_DESC">최신순</SelectItem>
            <SelectItem value="VIEW_DESC">조회순</SelectItem>
            <SelectItem value="OPENING_SOON">개막 예정</SelectItem>
          </SelectContent>
        </Select>
      </div>

      {/* 공연 목록 그리드 */}
      {!posts || posts.length === 0 ? (
        <div className="text-center py-10 text-gray-500">
          검색 결과가 없습니다.
        </div>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
          {posts?.map((show) => (
            <Link
              key={show.id}
              to={`/show/${show.id}`}
              className="bg-white rounded-lg overflow-hidden shadow-md hover:shadow-lg transition-shadow"
            >
              <div className="relative h-[380px]">
                <img
                  src={show.poster || "/placeholder.svg"}
                  alt={show.title}
                  className="object-cover z-10 w-full h-full"
                />
              </div>
              <div className="p-4">
                <h3 className="font-bold text-lg mb-1 line-clamp-1">
                  {show.title}
                </h3>
                <p className="text-gray-600 mb-2 text-sm">{show.venue}</p>
                <p className="text-gray-500 text-sm">
                  {getDurationStr(show.startDate, show.endDate)}
                </p>
              </div>
            </Link>
          ))}
        </div>
      )}

      {/* 페이지네이션 */}
      <div className="flex justify-center my-10">
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
  );
}
