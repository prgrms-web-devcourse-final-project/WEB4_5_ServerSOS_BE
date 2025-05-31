import { useEffect, useMemo, useRef, useState } from "react"
import { useLocation } from "react-router-dom"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { toast } from "@/components/ui/use-toast"
import { Calendar, MapPin, Clock, Ticket } from "lucide-react"
import type {
  PerformanceSessionResponse,
  PerformanceDetailResponse,
  PaymentDetailResponse,
} from "@/api/__generated__"
import { apiClient } from "@/api/apiClient"
import {
  loadTossPayments,
  type TossPaymentsWidgets,
} from "@tosspayments/tosspayments-sdk"
import { PageLayout } from "@/layout/PageLayout"

const clientKey = "test_gck_docs_Ovk5rk1EwkEbP0W43n07xlzm"
const customerKey = "1tptdcvwS0D4f1pgucMt4"

export default function PaymentPage() {
  const location = useLocation()
  const { selectedSeats, session, performance, reservationId, entryToken } =
    location.state as {
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
      reservationId: number
      entryToken: string
    }

  // 총 금액 계산
  const totalAmount = useMemo(
    () => selectedSeats.reduce((sum, seat) => sum + seat.price, 0),
    [selectedSeats],
  )
  const amount = useMemo(
    () => ({
      currency: "KRW",
      value: totalAmount,
    }),
    [totalAmount],
  )

  const [isProcessing, setIsProcessing] = useState(false)
  const [widgets, setWidgets] = useState<TossPaymentsWidgets | null>(null)

  const paymentDetailRef = useRef<PaymentDetailResponse | null>(null)

  useEffect(() => {
    async function fetchPaymentWidgets() {
      // ------  결제위젯 초기화 ------
      const tossPayments = await loadTossPayments(clientKey)
      // 회원 결제
      const widgets = tossPayments.widgets({
        customerKey,
      })
      // 비회원 결제
      // const widgets = tossPayments.widgets({ customerKey: ANONYMOUS });

      setWidgets(widgets)
    }

    fetchPaymentWidgets()
  }, [])

  useEffect(() => {
    async function renderPaymentWidgets() {
      if (widgets == null) {
        return
      }
      // ------ 주문의 결제 금액 설정 ------
      await widgets.setAmount(amount)

      await Promise.all([
        // ------  결제 UI 렌더링 ------
        widgets.renderPaymentMethods({
          selector: "#payment-method",
          variantKey: "DEFAULT",
        }),
        // ------  이용약관 UI 렌더링 ------
        widgets.renderAgreement({
          selector: "#agreement",
          variantKey: "AGREEMENT",
        }),
      ])
    }

    renderPaymentWidgets()
  }, [widgets, amount])

  useEffect(() => {
    if (widgets == null) {
      return
    }

    widgets.setAmount(amount)
  }, [widgets, amount])

  // biome-ignore lint/correctness/useExhaustiveDependencies: <explanation>
  useEffect(() => {
    async function createPayment() {
      const response = await apiClient.payment.createPayment(
        {
          paymentCreateRequest: {
            amount: totalAmount,
            reservationId,
          },
        },
        {
          headers: {
            EntryAuth: `Bearer ${entryToken}`,
            "Content-Type": "application/json",
          },
        },
      )

      const orderId = response.data?.orderId

      if (!response.data || !orderId) {
        throw new Error("결제 생성 실패")
      }

      paymentDetailRef.current = response.data
    }

    createPayment()
  }, [])

  // 결제 처리 함수 (구현은 나중에)
  const handlePayment = async () => {
    if (selectedSeats.length === 0) {
      toast({
        title: "선택된 좌석이 없습니다",
        description: "좌석을 선택한 후 결제를 진행해주세요.",
        variant: "destructive",
      })
      return
    }

    if (!paymentDetailRef.current || !paymentDetailRef.current.orderId) {
      toast({
        title: "결제 생성 실패",
        description: "결제 생성 실패",
        variant: "destructive",
      })
      return
    }

    if (widgets == null) {
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

      try {
        // ------ '결제하기' 버튼 누르면 결제창 띄우기 ------
        // 결제를 요청하기 전에 orderId, amount를 서버에 저장하세요.
        // 결제 과정에서 악의적으로 결제 금액이 바뀌는 것을 확인하는 용도입니다.
        await widgets.requestPayment({
          orderId: paymentDetailRef.current.orderId,
          orderName: `${performance?.name} 예매`,
          successUrl: `${window.location.origin}/payment/success`,
          failUrl: `${window.location.origin}/payment/fail`,
        })
      } catch (error) {
        // 에러 처리하기
        console.error(error)
      }
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
    <PageLayout>
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

            <p className="text-xs text-gray-500 text-center">
              결제 완료 후 예매 내역은 마이페이지에서 확인하실 수 있습니다.
            </p>
          </div>
        </div>

        {/* 결제 방법 */}
        {/* 결제 UI */}
        <div id="payment-method" />
        {/* 이용약관 UI */}
        <div id="agreement" />

        {/* 결제 버튼 */}
        <Button
          onClick={handlePayment}
          disabled={isProcessing || selectedSeats.length === 0}
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
      </div>
    </PageLayout>
  )
}
