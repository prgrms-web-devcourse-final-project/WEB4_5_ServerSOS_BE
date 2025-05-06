export default function ShowListSkeleton() {
  return (
    <div>
      {/* 정렬 옵션 스켈레톤 */}
      <div className="flex justify-end mb-6">
        <div className="w-[180px] h-10 bg-gray-200 rounded-md animate-pulse"></div>
      </div>

      {/* 공연 목록 그리드 스켈레톤 */}
      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
        {Array.from({ length: 8 }).map((_, index) => (
          <div
            key={index}
            className="bg-white rounded-lg overflow-hidden shadow-md"
          >
            <div className="h-[380px] bg-gray-200 animate-pulse"></div>
            <div className="p-4">
              <div className="h-6 bg-gray-200 rounded animate-pulse mb-2"></div>
              <div className="h-4 bg-gray-200 rounded animate-pulse w-2/3 mb-2"></div>
              <div className="h-4 bg-gray-200 rounded animate-pulse w-1/2"></div>
            </div>
          </div>
        ))}
      </div>

      {/* 페이지네이션 스켈레톤 */}
      <div className="flex justify-center mt-10">
        <div className="flex items-center space-x-2">
          <div className="w-10 h-10 bg-gray-200 rounded-md animate-pulse"></div>
          {Array.from({ length: 5 }).map((_, index) => (
            <div
              key={index}
              className="w-10 h-10 bg-gray-200 rounded-md animate-pulse"
            ></div>
          ))}
          <div className="w-10 h-10 bg-gray-200 rounded-md animate-pulse"></div>
        </div>
      </div>
    </div>
  )
}
