import { useState } from "react"
import { useLocation } from "react-router-dom"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { toast } from "@/components/ui/use-toast"
import { cn } from "@/lib/utils"
import {
  CreditCard,
  Calendar,
  MapPin,
  Clock,
  Ticket,
  Check,
  AlertCircle,
} from "lucide-react"
import type {
  PerformanceSessionResponse,
  PerformanceDetailResponse,
} from "@/api/__generated__"

export default function PaymentPage() {
  const location = useLocation()
  const { selectedSeats, session, performance } = location.state as {
    selectedSeats: {
      row: number
      col: number
      section: string
      sectionName: string
      rowLabel: string
      price: number
    }[]
    session: PerformanceSessionResponse
    performance?: PerformanceDetailResponse
  }

  const [isTermsAgreed, setIsTermsAgreed] = useState(false)
  const [isProcessing, setIsProcessing] = useState(false)

  // 총 금액 계산
  const totalAmount = selectedSeats.reduce((sum, seat) => sum + seat.price, 0)

  // 결제 처리 함수 (구현은 나중에)
  const handlePayment = async () => {
    if (!isTermsAgreed) {
      toast({
        title: "약관 동의가 필요합니다",
        description: "결제 서비스 이용 약관에 동의해주세요.",
        variant: "destructive",
      })
      return
    }

    if (selectedSeats.length === 0) {
      toast({
        title: "선택된 좌석이 없습니다",
        description: "좌석을 선택한 후 결제를 진행해주세요.",
        variant: "destructive",
      })
      return
    }

    setIsProcessing(true)

    try {
      // TODO: Toss Payments 결제 로직 구현
      console.log("결제 처리 시작", {
        sessionId: session.id,
        seats: selectedSeats,
        totalAmount,
      })

      // 임시 성공 처리
      await new Promise((resolve) => setTimeout(resolve, 2000))

      toast({
        title: "결제가 완료되었습니다",
        description: `${selectedSeats.length}석 예약이 완료되었습니다.`,
      })
    } catch (error) {
      toast({
        title: "결제 처리 중 오류가 발생했습니다",
        description: "잠시 후 다시 시도해주세요.",
        variant: "destructive",
      })
    } finally {
      setIsProcessing(false)
    }
  }

  // 날짜 포맷팅
  const formatDate = (date: Date | undefined) => {
    if (!date) return "-"
    return new Intl.DateTimeFormat("ko-KR", {
      year: "numeric",
      month: "long",
      day: "numeric",
      weekday: "short",
    }).format(new Date(date))
  }

  // 시간 포맷팅
  const formatTime = (date: Date | undefined) => {
    if (!date) return "-"
    return new Intl.DateTimeFormat("ko-KR", {
      hour: "2-digit",
      minute: "2-digit",
    }).format(new Date(date))
  }

  return (
    <div className="max-w-4xl mx-auto p-6 space-y-6">
      <div className="text-center mb-8">
        <h1 className="text-3xl font-bold text-gray-900 mb-2">결제하기</h1>
        <p className="text-gray-600">선택하신 좌석의 결제를 진행합니다</p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* 좌측: 공연 정보 및 좌석 정보 */}
        <div className="lg:col-span-2 space-y-6">
          {/* 공연 정보 */}
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Ticket className="w-5 h-5 text-primary" />
                공연 정보
              </CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="flex gap-4">
                {performance?.poster && (
                  <img
                    src={performance.poster}
                    alt={performance.name}
                    className="w-20 h-28 object-cover rounded-lg shadow-sm"
                  />
                )}
                <div className="flex-1 space-y-2">
                  <h3 className="text-xl font-semibold">
                    {performance?.name || "공연명"}
                  </h3>
                  <div className="space-y-1 text-sm text-gray-600">
                    <div className="flex items-center gap-2">
                      <MapPin className="w-4 h-4" />
                      <span>{performance?.venue?.name || "공연장"}</span>
                    </div>
                    <div className="flex items-center gap-2">
                      <Calendar className="w-4 h-4" />
                      <span>{formatDate(session.time)}</span>
                    </div>
                    <div className="flex items-center gap-2">
                      <Clock className="w-4 h-4" />
                      <span>{formatTime(session.time)}</span>
                    </div>
                  </div>
                </div>
              </div>
            </CardContent>
          </Card>

          {/* 선택한 좌석 정보 */}
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Ticket className="w-5 h-5 text-primary" />
                선택한 좌석 ({selectedSeats.length}석)
              </CardTitle>
            </CardHeader>
            <CardContent>
              <div className="space-y-3">
                {selectedSeats.map((seat, index) => (
                  <div
                    key={index}
                    className="flex justify-between items-center py-3 px-4 bg-gray-50 rounded-lg"
                  >
                    <div className="flex items-center gap-3">
                      <div className="w-8 h-8 bg-primary/10 rounded-full flex items-center justify-center">
                        <span className="text-sm font-medium text-primary">
                          {index + 1}
                        </span>
                      </div>
                      <div>
                        <div className="font-medium">{seat.sectionName}</div>
                        <div className="text-sm text-gray-600">
                          {seat.rowLabel}행 {seat.col + 1}번
                        </div>
                      </div>
                    </div>
                    <div className="text-right">
                      <div className="font-semibold">
                        {seat.price.toLocaleString()}원
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>
        </div>

        {/* 우측: 결제 정보 */}
        <div className="space-y-6">
          {/* 결제 금액 */}
          <Card>
            <CardHeader>
              <CardTitle>결제 금액</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="space-y-2">
                <div className="flex justify-between text-sm">
                  <span>좌석 요금</span>
                  <span>{totalAmount.toLocaleString()}원</span>
                </div>
                <div className="flex justify-between text-sm">
                  <span>예매 수수료</span>
                  <span>0원</span>
                </div>
                <hr className="my-2" />
                <div className="flex justify-between font-bold text-lg">
                  <span>총 결제 금액</span>
                  <span className="text-primary">
                    {totalAmount.toLocaleString()}원
                  </span>
                </div>
              </div>
            </CardContent>
          </Card>

          {/* 결제 방법 */}
          <Card>
            <CardHeader>
              <CardTitle>결제 방법</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="flex items-center gap-3 p-4 border rounded-lg bg-blue-50 border-blue-200">
                <CreditCard className="w-6 h-6 text-blue-600" />
                <div className="flex-1">
                  <div className="font-medium text-blue-900">Toss Payments</div>
                  <div className="text-sm text-blue-700">
                    간편하고 안전한 결제
                  </div>
                </div>
                <div className="w-6 h-6 bg-blue-600 rounded-full flex items-center justify-center">
                  <Check className="w-4 h-4 text-white" />
                </div>
              </div>
            </CardContent>
          </Card>

          {/* 약관 동의 */}
          <Card>
            <CardContent className="pt-6">
              <div className="space-y-4">
                <div className="flex items-start gap-3">
                  <button
                    type="button"
                    onClick={() => setIsTermsAgreed(!isTermsAgreed)}
                    className={cn(
                      "w-5 h-5 rounded border-2 flex items-center justify-center transition-colors",
                      isTermsAgreed
                        ? "bg-primary border-primary"
                        : "border-gray-300 hover:border-gray-400",
                    )}
                  >
                    {isTermsAgreed && <Check className="w-3 h-3 text-white" />}
                  </button>
                  <div className="flex-1">
                    <label
                      htmlFor="terms"
                      className="text-sm font-medium cursor-pointer"
                    >
                      [필수] 결제 서비스 이용 약관, 개인정보 처리 동의
                    </label>
                    <p className="text-xs text-gray-500 mt-1">
                      결제 진행을 위해 약관에 동의해주세요.
                    </p>
                  </div>
                </div>

                {!isTermsAgreed && (
                  <div className="flex items-center gap-2 text-amber-600 text-sm">
                    <AlertCircle className="w-4 h-4" />
                    <span>약관 동의가 필요합니다</span>
                  </div>
                )}
              </div>
            </CardContent>
          </Card>

          {/* 결제 버튼 */}
          <Button
            onClick={handlePayment}
            disabled={
              !isTermsAgreed || isProcessing || selectedSeats.length === 0
            }
            className="w-full h-12 text-lg font-semibold"
            size="lg"
          >
            {isProcessing ? (
              <div className="flex items-center gap-2">
                <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
                결제 처리 중...
              </div>
            ) : (
              `${totalAmount.toLocaleString()}원 결제하기`
            )}
          </Button>

          <p className="text-xs text-gray-500 text-center">
            결제 완료 후 예매 내역은 마이페이지에서 확인하실 수 있습니다.
          </p>
        </div>
      </div>
    </div>
  )
}
