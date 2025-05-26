import { useEffect, useState } from "react"
import { useSearchParams, useNavigate } from "react-router-dom"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { CheckCircle, Home, Ticket, Loader2 } from "lucide-react"
import { apiClient } from "@/api/apiClient"

export default function PaymentSuccess() {
  const [searchParams] = useSearchParams()
  const navigate = useNavigate()
  const [isConfirm, setIsConfirm] = useState(false)

  // URL 파라미터에서 결제 정보 추출
  const paymentKey = searchParams.get("paymentKey")
  const orderId = searchParams.get("orderId")
  const amount = searchParams.get("amount")
  const paymentType = searchParams.get("paymentType")

  useEffect(() => {
    // 쿼리 파라미터 값이 결제 요청할 때 보낸 데이터와 동일한지 반드시 확인하세요.
    // 클라이언트에서 결제 금액을 조작하는 행위를 방지할 수 있습니다.

    console.log(paymentKey, orderId, amount, paymentType)
    async function confirm() {
      if (!paymentKey || !orderId || !amount || !paymentType) {
        return
      }

      const response = await apiClient.payment.confirmPayment({
        paymentConfirmRequest: {
          amount: Number.parseInt(amount ?? "0"),
          orderId: orderId,
          paymentKey: paymentKey,
        },
      })

      if (response.code !== 200) {
        return
      }

      setIsConfirm(true)
    }

    confirm()
  }, [paymentKey, orderId, amount, paymentType])

  const handleGoHome = () => {
    navigate("/")
  }

  const handleGoToMyReservations = () => {
    navigate("/my")
  }

  // 결제 확인 중일 때 로딩 화면
  if (!isConfirm) {
    return (
      <div className="max-w-2xl mx-auto p-6 space-y-6">
        <div className="text-center space-y-4">
          {/* 로딩 아이콘 */}
          <div className="flex justify-center">
            <div className="w-20 h-20 bg-blue-100 rounded-full flex items-center justify-center">
              <Loader2 className="w-12 h-12 text-blue-600 animate-spin" />
            </div>
          </div>

          {/* 로딩 메시지 */}
          <div className="space-y-2">
            <h1 className="text-3xl font-bold text-gray-900">
              결제 정보를 확인중입니다
            </h1>
            <p className="text-gray-600">잠시만 기다려주세요...</p>
          </div>
        </div>
      </div>
    )
  }

  // 결제 확인 완료 후 성공 화면
  return (
    <div className="max-w-2xl mx-auto p-6 space-y-6">
      <div className="text-center space-y-4">
        {/* 성공 아이콘 */}
        <div className="flex justify-center">
          <div className="w-20 h-20 bg-green-100 rounded-full flex items-center justify-center">
            <CheckCircle className="w-12 h-12 text-green-600" />
          </div>
        </div>

        {/* 성공 메시지 */}
        <div className="space-y-2">
          <h1 className="text-3xl font-bold text-gray-900">
            결제를 완료했어요!
          </h1>
          <p className="text-gray-600">예매가 성공적으로 완료되었습니다.</p>
        </div>
      </div>

      {/* 결제 정보 카드 */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Ticket className="w-5 h-5 text-primary" />
            결제 정보
          </CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="space-y-3">
            <div className="flex justify-between items-center py-2 border-b border-gray-100">
              <span className="text-gray-600">결제 금액</span>
              <span className="font-semibold text-lg">
                {Number.parseInt(amount ?? "0").toLocaleString()}원
              </span>
            </div>
            <div className="flex justify-between items-center py-2 border-b border-gray-100">
              <span className="text-gray-600">주문번호</span>
              <span className="font-mono text-sm">{orderId}</span>
            </div>
            <div className="flex justify-between items-center py-2">
              <span className="text-gray-600">결제키</span>
              <span className="font-mono text-sm text-gray-500 break-all">
                {paymentKey}
              </span>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* 안내 메시지 */}
      <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
        <p className="text-blue-800 text-sm">
          📧 예매 확인서가 등록하신 이메일로 발송됩니다.
          <br />📱 예매 내역은 마이페이지에서 확인하실 수 있습니다.
        </p>
      </div>

      {/* 버튼 그룹 */}
      <div className="flex flex-col sm:flex-row gap-3">
        <Button
          onClick={handleGoHome}
          variant="outline"
          className="flex-1 h-12 text-base"
        >
          <Home className="w-4 h-4 mr-2" />
          홈으로
        </Button>
        <Button
          onClick={handleGoToMyReservations}
          className="flex-1 h-12 text-base"
        >
          <Ticket className="w-4 h-4 mr-2" />내 예매 내역
        </Button>
      </div>
    </div>
  )
}
