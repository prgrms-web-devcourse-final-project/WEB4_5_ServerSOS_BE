import { useEffect, useState } from "react"
import { PageLayout } from "../layout/PageLayout"
import SeatMap from "@/components/reservation/SeatMap"
import { useUser } from "@/hooks/useUser"
import { useNavigate, useParams } from "react-router-dom"
import { useSubscriptionEnterQueue } from "@/hooks/useSubscriptionEnterQueue"

export const ShowReservation = () => {
  const { isLogin } = useUser()
  const navigate = useNavigate()
  const { id } = useParams()
  const [waitInQueue, setWaitInQueue] = useState<{
    status: "waiting" | "success" | "error"
    position: number
  }>({
    status: "waiting",
    position: 0,
  })

  useEffect(() => {
    if (!isLogin) {
      navigate(`/login?redirect=/show/${id}/reservation`)
      return
    }
  }, [isLogin, navigate, id])

  useSubscriptionEnterQueue({
    disabled: !isLogin,
    onMessage: (data: { position: number }, cleanup) => {
      if (data.position === 0) {
        cleanup()
      }

      setWaitInQueue({
        status: data.position > 0 ? "waiting" : "success",
        position: data.position,
      })
    },
    onError: (err) => {
      console.error("SSE 에러:", err)
      setWaitInQueue({
        status: "error",
        position: 0,
      })
    },
  })

  return (
    <PageLayout>
      <div className="container mx-auto py-8 px-4">
        <h1 className="text-3xl font-bold text-center mb-8">좌석 예매</h1>

        <div className="max-w-7xl mx-auto">
          {waitInQueue.status === "waiting" ? (
            <div className="flex flex-col items-center justify-center py-20">
              <div className="mb-6">
                <span className="inline-block w-12 h-12 border-4 border-blue-400 border-t-transparent rounded-full animate-spin"></span>
              </div>
              <div className="text-xl font-semibold text-gray-700 mb-2">
                대기열에 입장 중입니다...
              </div>
              {waitInQueue.position > 0 && (
                <div className="text-lg text-gray-500">
                  현재 대기 순서:{" "}
                  <span className="font-bold text-blue-600">
                    {waitInQueue.position}
                  </span>{" "}
                  번
                </div>
              )}
            </div>
          ) : (
            <SeatMap />
          )}
        </div>
      </div>
    </PageLayout>
  )
}
