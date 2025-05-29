import { useNavigate, useParams } from "react-router-dom"

import { useState, useEffect, useMemo } from "react"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import SimpleCalendar from "@/components/ui/simple-calendar"
import ShowInfo from "@/components/show/ShowInfo"
import ReviewSection from "@/components/review/ReviewSection"
import { PageLayout } from "@/layout/PageLayout"
import { usePostDetail } from "@/hooks/usePostDetail"
import { getDurationStr } from "@/lib/date"
import type { PerformanceSessionResponse } from "@/api/__generated__"
import CategoryNavigation from "@/components/category/CategoryNavigation"

export function ShowDetail() {
  const { id } = useParams()
  const navigate = useNavigate()

  const [selectedDate, setSelectedDate] = useState<Date | undefined>(undefined)
  const [filteredSessions, setFilteredSessions] = useState<
    PerformanceSessionResponse[]
  >([])
  const [selectedSession, setSelectedSession] = useState<
    PerformanceSessionResponse | undefined
  >(undefined)
  const { post: showData, isLoading, error } = usePostDetail({ id: Number(id) })

  const isThirtyDaysBeforeStart = useMemo(() => {
    if (!showData?.performance?.startDate) {
      return false
    }

    const today = new Date()
    const thirtyDaysBeforeStart = new Date(showData.performance.startDate)
    thirtyDaysBeforeStart.setDate(showData.performance.startDate.getDate() - 30)

    return today < thirtyDaysBeforeStart
  }, [showData])

  // 날짜 선택 핸들러
  const handleDateSelect = (selectedDate: Date) => {
    if (!showData?.performance?.sessions) {
      return
    }

    const sessions = showData.performance.sessions

    // 공연 시작일보다 30일 이전 날짜 선택 시 alert

    if (isThirtyDaysBeforeStart) {
      alert("공연 시작일보다 30일 이전 날짜는 선택할 수 없습니다.")
      return
    }

    console.log("선택된 날짜:", selectedDate.toDateString())
    setSelectedDate(selectedDate)
    setSelectedSession(undefined)

    // 선택된 날짜와 같은 날짜의 세션들 필터링
    const filteredSessions = sessions.filter((session) => {
      if (!session.time) return false

      const sessionDate = new Date(session.time)

      // 년월일만 비교 (시간 제거)
      const selectedDateOnly = new Date(
        selectedDate.getFullYear(),
        selectedDate.getMonth(),
        selectedDate.getDate(),
      )
      const sessionDateOnly = new Date(
        sessionDate.getFullYear(),
        sessionDate.getMonth(),
        sessionDate.getDate(),
      )

      return selectedDateOnly.getTime() === sessionDateOnly.getTime()
    })

    console.log("필터링된 세션들:", filteredSessions)
    setFilteredSessions(filteredSessions)
  }

  const handleReservationClick = () => {
    if (!showData?.id || !selectedSession) {
      return
    }

    // 선택된 세션 정보와 함께 예매 페이지로 이동
    navigate(`/show/${showData.id}/reservation`, {
      state: {
        selectedSession,
        selectedDate,
      },
    })
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
      <CategoryNavigation />
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
                    {showData.performance?.minAge}
                  </div>
                </div>
                <div className="grid grid-cols-3 gap-2 border-b pb-2">
                  <div className="font-medium">가격</div>
                  <div className="col-span-2">
                    {showData.performance?.areas?.length &&
                    showData.performance?.areas?.length > 0 ? (
                      <div className="space-y-2">
                        <table className="w-full text-sm">
                          <thead>
                            <tr className="border-b">
                              <th className="text-left py-1">좌석 구역</th>
                              <th className="text-right py-1">가격</th>
                            </tr>
                          </thead>
                          <tbody>
                            {showData.performance.areas.map((area, index) => (
                              <tr
                                key={index}
                                className="border-b border-gray-100"
                              >
                                <td className="py-1">
                                  {area.name || `구역 ${index + 1}`}
                                </td>
                                <td className="text-right py-1">
                                  {area.price
                                    ? `${area.price.toLocaleString()}원`
                                    : "가격 미정"}
                                </td>
                              </tr>
                            ))}
                          </tbody>
                        </table>
                      </div>
                    ) : (
                      "정보 없음"
                    )}
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
                {/* 필터링된 세션들을 시간 선택으로 표시 */}
                {selectedDate && filteredSessions.length > 0 && (
                  <div className="mt-4">
                    <h4 className="text-sm font-medium mb-2 text-center">
                      공연 시간 선택
                    </h4>
                    <div className="grid grid-cols-2 gap-2">
                      {filteredSessions.map((session) => (
                        <button
                          key={session.id}
                          onClick={() => setSelectedSession(session)}
                          className={`px-3 py-2 text-xs rounded border transition-colors ${
                            selectedSession?.id === session.id
                              ? "bg-slate-900 text-white border-slate-900"
                              : "bg-white text-slate-700 border-slate-300 hover:border-slate-400"
                          }`}
                        >
                          {session.time
                            ? new Date(session.time).toLocaleTimeString(
                                "ko-KR",
                                {
                                  hour: "2-digit",
                                  minute: "2-digit",
                                  hour12: false,
                                },
                              )
                            : "시간 미정"}
                        </button>
                      ))}
                    </div>
                  </div>
                )}
                {selectedDate && filteredSessions.length === 0 && (
                  <div className="mt-4 text-center text-sm text-slate-500">
                    선택한 날짜에 공연이 없습니다.
                  </div>
                )}
                {isThirtyDaysBeforeStart && (
                  <div className="mt-4 text-center text-sm text-slate-500">
                    공연 시작일보다 30일 이전에는 예매가 불가능합니다.
                  </div>
                )}
                <div className="mt-6">
                  {selectedDate && selectedSession ? (
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
                      {selectedDate
                        ? "시간을 선택해주세요"
                        : "날짜를 선택해주세요"}
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
                detailImages={showData.performance?.images ?? []}
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
