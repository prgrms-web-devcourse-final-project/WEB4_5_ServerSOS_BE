import { useNavigate, useParams } from "react-router-dom"

import { useState, useEffect } from "react"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import SimpleCalendar from "@/components/ui/simple-calendar"
import ShowInfo from "@/components/show/ShowInfo"
import ReviewSection from "@/components/review/ReviewSection"
import { PageLayout } from "@/layout/PageLayout"
import { usePostDetail } from "@/hooks/usePostDetail"
import { getDurationStr } from "@/lib/date"

export function ShowDetail() {
  const { id } = useParams()
  const navigate = useNavigate()

  const [selectedDate, setSelectedDate] = useState<Date | undefined>(undefined)
  const { post: showData, isLoading, error } = usePostDetail({ id: Number(id) })

  // 날짜 선택 핸들러
  const handleDateSelect = (date: Date) => {
    console.log("선택된 날짜:", date.toDateString())
    setSelectedDate(date)
  }

  const handleReservationClick = () => {
    if (!showData?.id) {
      return
    }

    // 날짜 유효성 검사
    const startDateStr = showData.performance?.startDate
    const endDateStr = showData.performance?.endDate
    if (!selectedDate || !startDateStr || !endDateStr) {
      alert("날짜 정보가 올바르지 않습니다.")
      return
    }
    const startDate = new Date(startDateStr)
    const endDate = new Date(endDateStr)
    // 선택된 날짜가 공연 기간 내에 있는지 확인
    if (selectedDate < startDate || selectedDate > endDate) {
      alert("선택한 날짜가 공연 기간 내에 있지 않습니다.")
      return
    }

    navigate(`/show/${showData.id}/reservation`)
  }

  // 디버깅용 - 선택된 날짜 변경 감지
  useEffect(() => {
    console.log(
      "선택된 날짜 상태:",
      selectedDate ? selectedDate.toDateString() : "없음",
    )
  }, [selectedDate])

  if (!id) {
    return <div>ID가 없습니다.</div>
  }

  if (isLoading) {
    return <div>Loading...</div>
  }

  if (error) {
    return <div>Error: {error.message}</div>
  }

  if (!showData || !showData.id) {
    return <div>데이터가 없습니다</div>
  }

  return (
    <PageLayout>
      {/* 공연 상세 정보 */}
      <div className="container mx-auto px-4 py-8">
        <h1 className="text-3xl font-bold mb-8">{showData.title}</h1>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          {/* 포스터 */}
          <div className="md:col-span-1">
            <div className="relative aspect-[2/3] rounded-lg overflow-hidden shadow-md">
              <img
                src={showData.performance?.poster || "/placeholder.svg"}
                alt={showData.title}
                className="object-cover h-full"
              />
            </div>
          </div>

          {/* 공연 정보 및 캘린더 */}
          <div className="md:col-span-2">
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
              {/* 공연 정보 */}
              <div className="space-y-4">
                <div className="grid grid-cols-3 gap-2 border-b pb-2">
                  <div className="font-medium">장소</div>
                  <div className="col-span-2">
                    {showData.performance?.venue?.name}
                  </div>
                </div>
                <div className="grid grid-cols-3 gap-2 border-b pb-2">
                  <div className="font-medium">주소</div>
                  <div className="col-span-2">
                    {showData.performance?.venue?.address}
                  </div>
                </div>
                <div className="grid grid-cols-3 gap-2 border-b pb-2">
                  <div className="font-medium">공연 기간</div>
                  <div className="col-span-2">
                    {getDurationStr(
                      showData.performance?.startDate,
                      showData.performance?.endDate,
                    )}
                  </div>
                </div>
                <div className="grid grid-cols-3 gap-2 border-b pb-2">
                  <div className="font-medium">공연 시간</div>
                  <div className="col-span-2">
                    {showData.performance?.runtime}
                  </div>
                </div>
                <div className="grid grid-cols-3 gap-2 border-b pb-2">
                  <div className="font-medium">관람 연령</div>
                  <div className="col-span-2">
                    최소 {showData.performance?.minAge}세
                  </div>
                </div>
                <div className="grid grid-cols-3 gap-2 border-b pb-2">
                  <div className="font-medium">가격</div>
                  <div className="col-span-2">
                    {showData.performance?.areas?.length &&
                    showData.performance?.areas?.length > 0
                      ? `${Math.min(
                          ...showData.performance.areas.map(
                            (area) => area.price ?? Number.POSITIVE_INFINITY,
                          ),
                        ).toLocaleString()}원`
                      : "정보 없음"}
                  </div>
                </div>
                <div className="grid grid-cols-3 gap-2 border-b pb-2">
                  <div className="font-medium">상태</div>
                  <div className="col-span-2">
                    {showData.performance?.state}
                  </div>
                </div>
                {/* <div className="grid grid-cols-3 gap-2 border-b pb-2">
                  <div className="font-medium">제작사</div>
                  <div className="col-span-2">
                    {showData.performance?.}
                  </div>
                </div> */}
              </div>

              {/* 캘린더 */}
              <div className="bg-white rounded-lg border p-4">
                <h3 className="text-lg font-medium mb-4 text-center">관람일</h3>
                <SimpleCalendar
                  onSelectDate={handleDateSelect}
                  selectedDate={selectedDate}
                />
                <div className="mt-6">
                  {selectedDate ? (
                    <button
                      className="w-full inline-flex justify-center items-center h-11 px-8 py-2 rounded-md text-sm font-medium transition-colors bg-slate-900 text-slate-50 hover:bg-slate-900/90 cursor-pointer"
                      onClick={handleReservationClick}
                    >
                      예매하기
                    </button>
                  ) : (
                    <button
                      disabled
                      className="w-full inline-flex justify-center items-center h-11 px-8 py-2 rounded-md text-sm font-medium bg-slate-300 text-slate-500 cursor-not-allowed"
                    >
                      예매하기
                    </button>
                  )}
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* 탭 (공연 정보, 리뷰) */}
        <div className="mt-12">
          <Tabs defaultValue="info" className="w-full">
            <TabsList className="grid w-full grid-cols-2 mb-8">
              <TabsTrigger value="info">공연 정보</TabsTrigger>
              <TabsTrigger value="reviews">리뷰</TabsTrigger>
            </TabsList>
            <TabsContent value="info">
              <ShowInfo
                description={showData.content ?? ""}
                detailImages={showData.performance?.introImages ?? []}
              />
            </TabsContent>
            <TabsContent value="reviews">
              <ReviewSection showId={showData.id} />
            </TabsContent>
          </Tabs>
        </div>
      </div>
    </PageLayout>
  )
}
