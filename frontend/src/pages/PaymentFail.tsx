import { useEffect, useState } from "react"
import { useSearchParams, useNavigate } from "react-router-dom"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { XCircle, Home, ArrowLeft, AlertTriangle } from "lucide-react"

export default function PaymentFail() {
  const [searchParams] = useSearchParams()
  const navigate = useNavigate()

  // URL 파라미터에서 에러 정보 추출
  const errorCode = searchParams.get("code")
  const errorMessage = searchParams.get("message")
  const orderId = searchParams.get("orderId")

  const [errorInfo, setErrorInfo] = useState<{
    code: string
    message: string
    orderId?: string
  } | null>(null)

  useEffect(() => {
    if (errorCode && errorMessage) {
      setErrorInfo({
        code: errorCode,
        message: errorMessage,
        orderId: orderId || undefined,
      })
    }
  }, [errorCode, errorMessage, orderId])

  const handleGoHome = () => {
    navigate("/")
  }

  const handleGoBack = () => {
    navigate(-1)
  }

  return (
    <div className="max-w-2xl mx-auto p-6 space-y-6">
      <div className="text-center space-y-4">
        {/* 실패 아이콘 */}
        <div className="flex justify-center">
          <div className="w-20 h-20 bg-red-100 rounded-full flex items-center justify-center">
            <XCircle className="w-12 h-12 text-red-600" />
          </div>
        </div>

        {/* 실패 메시지 */}
        <div className="space-y-2">
          <h1 className="text-3xl font-bold text-gray-900">
            결제를 실패했어요
          </h1>
          <p className="text-gray-600">결제 처리 중 문제가 발생했습니다.</p>
        </div>
      </div>

      {/* 에러 정보 카드 */}
      {errorInfo && (
        <Card className="border-red-200">
          <CardHeader>
            <CardTitle className="flex items-center gap-2 text-red-700">
              <AlertTriangle className="w-5 h-5" />
              오류 정보
            </CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="space-y-3">
              <div className="flex justify-between items-start py-2 border-b border-gray-100">
                <span className="text-gray-600">오류 메시지</span>
                <span className="text-red-600 text-right max-w-xs break-words">
                  {errorInfo.message}
                </span>
              </div>
              <div className="flex justify-between items-center py-2 border-b border-gray-100">
                <span className="text-gray-600">오류 코드</span>
                <span className="font-mono text-sm text-red-600">
                  {errorInfo.code}
                </span>
              </div>
              {errorInfo.orderId && (
                <div className="flex justify-between items-center py-2">
                  <span className="text-gray-600">주문번호</span>
                  <span className="font-mono text-sm text-gray-500">
                    {errorInfo.orderId}
                  </span>
                </div>
              )}
            </div>
          </CardContent>
        </Card>
      )}

      {/* 안내 메시지 */}
      <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-4">
        <p className="text-yellow-800 text-sm">
          💡 결제가 실패한 경우 다음을 확인해주세요:
          <br />• 카드 한도 및 잔액 확인
          <br />• 카드 정보 입력 오류
          <br />• 네트워크 연결 상태
          <br />
          문제가 지속되면 고객센터로 문의해주세요.
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
        <Button onClick={handleGoBack} className="flex-1 h-12 text-base">
          <ArrowLeft className="w-4 h-4 mr-2" />
          이전으로
        </Button>
      </div>
    </div>
  )
}
