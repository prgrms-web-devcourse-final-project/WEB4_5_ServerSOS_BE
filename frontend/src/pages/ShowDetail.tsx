import { Link, useParams } from "react-router-dom"

import { useState, useEffect } from "react"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import SimpleCalendar from "@/components/ui/simple-calendar"
import ShowInfo from "@/components/show/ShowInfo"
import ReviewSection from "@/components/review/ReviewSection"

// 공연 데이터 목업
const getShowData = (id: string) => {
  // 실제 구현에서는 API 호출로 대체
  return {
    id: Number.parseInt(id),
    title: "2025 권진아 단독 콘서트 [The Dreamest]",
    venue: "잠실실내체육관",
    address: "서울특별시 송파구 올림픽로 25",
    startDate: "2025.05.10",
    endDate: "2025.05.11",
    duration: "1시간 30분",
    ageLimit: "만 7세이상",
    price: "154,000원",
    status: "상영중",
    producer: "국단 피에로",
    posterUrl: "/placeholder.svg?key=2hmip",
    description: `
      <h3>캐스팅</h3>
      <p>권진아, 권진아2, 권진아3</p>
      
      <h3>공지사항</h3>
      <p>※ 티켓 예매 시 공연 안내사항에 동의한 것으로 간주하며, 본 내용은 상황에 따라 추가/변경될 수 있습니다. 공연 관람에 지장이나 불이익을 받지 않도록 관람 전 반드시 공연 안내사항을 재확인 바랍니다.</p>
      <p>※ 입장 및 예매 안내</p>
      <p>※ 인증 수단 : '예술의전당 유료회원(성인회원, 어린이 등) 으로가입 및 부분이 완료된 입장객 분들만 주차할 수 있기 바랍니다.</p>
      <p>※ 공연 당일 : '현장 입장'으로 예매가 끝난 예매자 분들은 공연 시간(11:59:59PM)까지 입장하셔야 예매가 확정되지 않습니다. (단, 온라인 및 현장예약이 완료되신 11:30PM까지는 입장 불가드립니다.)</p>
      
      <h3>[현장에서 예매 안내]</h3>
      <p>◎ 현장에서 구매는 인터파크 고객센터(1544-1555)를 통한 전화예매만 가능합니다 (고객센터 운영시간 오전 9시~오후 6시)</p>
      <p>◎ 사생한 사항은 하단 SNS페이지 내 '현장에서 예매 안내'를 참고해 주시기 바랍니다.</p>
      
      <p>※ 티켓 분실, 파손 등 어떠한 경우에도 재발권 및 입장이 불가능하오니, 티켓관리에 유의하시기 바랍니다.</p>
    `,
    detailImages: [
      "/placeholder.svg?key=e4dvp",
      "/placeholder.svg?key=w5wyn",
      "/placeholder.svg?key=vrp6p",
    ],
  }
}

export function ShowDetail() {
  const { id } = useParams()

  if (!id) {
    return <div>ID가 없습니다.</div>
  }

  const [selectedDate, setSelectedDate] = useState<Date | undefined>(undefined)
  const showData = getShowData(id)

  // 날짜 선택 핸들러
  const handleDateSelect = (date: Date) => {
    console.log("선택된 날짜:", date.toDateString())
    setSelectedDate(date)
  }

  // 디버깅용 - 선택된 날짜 변경 감지
  useEffect(() => {
    console.log(
      "선택된 날짜 상태:",
      selectedDate ? selectedDate.toDateString() : "없음",
    )
  }, [selectedDate])

  return (
    <main className="min-h-screen bg-white">
      {/* 공연 상세 정보 */}
      <div className="container mx-auto px-4 py-8">
        <h1 className="text-3xl font-bold mb-8">{showData.title}</h1>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          {/* 포스터 */}
          <div className="md:col-span-1">
            <div className="relative aspect-[2/3] rounded-lg overflow-hidden shadow-md">
              <img
                src={showData.posterUrl || "/placeholder.svg"}
                alt={showData.title}
                className="object-cover"
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
                  <div className="col-span-2">{showData.venue}</div>
                </div>
                <div className="grid grid-cols-3 gap-2 border-b pb-2">
                  <div className="font-medium">주소</div>
                  <div className="col-span-2">{showData.address}</div>
                </div>
                <div className="grid grid-cols-3 gap-2 border-b pb-2">
                  <div className="font-medium">공연 기간</div>
                  <div className="col-span-2">
                    {showData.startDate} ~ {showData.endDate}
                  </div>
                </div>
                <div className="grid grid-cols-3 gap-2 border-b pb-2">
                  <div className="font-medium">공연 시간</div>
                  <div className="col-span-2">{showData.duration}</div>
                </div>
                <div className="grid grid-cols-3 gap-2 border-b pb-2">
                  <div className="font-medium">관람 연령</div>
                  <div className="col-span-2">{showData.ageLimit}</div>
                </div>
                <div className="grid grid-cols-3 gap-2 border-b pb-2">
                  <div className="font-medium">가격</div>
                  <div className="col-span-2">{showData.price}</div>
                </div>
                <div className="grid grid-cols-3 gap-2 border-b pb-2">
                  <div className="font-medium">상태</div>
                  <div className="col-span-2">{showData.status}</div>
                </div>
                <div className="grid grid-cols-3 gap-2 border-b pb-2">
                  <div className="font-medium">제작사</div>
                  <div className="col-span-2">{showData.producer}</div>
                </div>
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
                    <Link
                      to={`/show/${showData.id}/reservation`}
                      className="w-full inline-flex justify-center items-center h-11 px-8 py-2 rounded-md text-sm font-medium transition-colors bg-slate-900 text-slate-50 hover:bg-slate-900/90"
                    >
                      예매하기
                    </Link>
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
                description={showData.description}
                detailImages={showData.detailImages}
              />
            </TabsContent>
            <TabsContent value="reviews">
              <ReviewSection showId={showData.id} />
            </TabsContent>
          </Tabs>
        </div>
      </div>
    </main>
  )
}
