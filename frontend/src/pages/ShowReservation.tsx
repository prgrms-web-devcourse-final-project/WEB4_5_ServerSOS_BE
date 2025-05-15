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
    onMessage: (data: { position: number }) => {
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
          <SeatMap />
        </div>
      </div>
    </PageLayout>
  )
}
