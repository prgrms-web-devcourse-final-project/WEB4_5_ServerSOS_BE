import { useEffect, useState } from "react"
import { PageLayout } from "../layout/PageLayout"
import SeatMap from "@/components/reservation/SeatMap"
import { useUser } from "@/hooks/useUser"
import { useLocation, useNavigate, useParams } from "react-router-dom"
import { useSubscriptionEnterQueue } from "@/hooks/useSubscriptionEnterQueue"
import { usePostDetail } from "@/hooks/usePostDetail"
import type { PerformanceSessionResponse } from "@/api/__generated__"

export const ShowReservation = () => {
  const { checkLogin } = useUser()
  const navigate = useNavigate()
  const location = useLocation()
  const { id } = useParams()
  const { post: showData } = usePostDetail({ id: Number(id) })
  const [waitInQueue, setWaitInQueue] = useState<
    | {
        status: "waiting" | "error"
        position: number
        totalCount: number
        estimatedTime: string
      }
    | {
        status: "success"
        entryToken: string
      }
  >({
    status: "waiting",
    position: 0,
    totalCount: 0,
    estimatedTime: "",
  })

  const state = location.state as
    | {
        selectedSession: PerformanceSessionResponse
        selectedDate: Date
      }
    | undefined

  useEffect(() => {
    if (!checkLogin()) {
      navigate(`/login?redirect=/show/${id}/reservation`)
      return
    }
  }, [checkLogin, navigate, id])

  useEffect(() => {
    console.log("selectedSession", state?.selectedSession)

    if (!state?.selectedSession?.id) {
      navigate(`/show/${id}`)
    }
  }, [state, navigate, id])

  useSubscriptionEnterQueue({
    sessionId: state?.selectedSession?.id,
    disabled: !checkLogin() || waitInQueue.status === "success",
    onMessage: (
      data: {
        position: number
        totalCount: number
        estimatedTime: string
        entryToken: string
      },
      cleanup,
    ) => {
      if (!data.entryToken) {
        setWaitInQueue({
          status: "waiting",
          position: data.position,
          totalCount: data.totalCount,
          estimatedTime: data.estimatedTime,
        })

        return
      }

      setWaitInQueue({
        status: "success",
        entryToken: data.entryToken,
      })

      cleanup()
    },
    onError: (err) => {
      console.error("SSE 에러:", err)
      setWaitInQueue({
        status: "error",
        position: 0,
        totalCount: 0,
        estimatedTime: "",
      })
    },
  })

  if (!state?.selectedSession?.id) {
    return <div>Loading...</div>
  }

  return (
    <PageLayout>
      <div className="container mx-auto py-8 px-4">
        <h1 className="text-3xl font-bold text-center mb-8">좌석 예매</h1>

        <div className="max-w-7xl mx-auto">
          {waitInQueue.status === "waiting" && (
            <div className="flex flex-col items-center justify-center py-20">
              <div className="mb-6">
                <span className="inline-block w-12 h-12 border-4 border-blue-400 border-t-transparent rounded-full animate-spin"></span>
              </div>
              <div className="text-xl font-semibold text-gray-700 mb-2">
                대기열에 입장 중입니다...
              </div>
              {waitInQueue.position > 0 && (
                <>
                  <div className="text-lg text-gray-500">
                    현재 대기 순서:{" "}
                    <span className="font-bold text-blue-600">
                      {waitInQueue.position}
                    </span>{" "}
                    번
                  </div>
                  <div className="text-lg text-gray-500">
                    총 대기 인원:{" "}
                    <span className="font-bold text-blue-600">
                      {waitInQueue.totalCount}
                    </span>
                  </div>
                </>
              )}
              {waitInQueue.estimatedTime && (
                <div className="text-lg text-gray-500">
                  예상 대기 시간: {waitInQueue.estimatedTime}
                </div>
              )}
            </div>
          )}
          {waitInQueue.status === "success" && (
            <SeatMap
              session={state?.selectedSession}
              performance={showData?.performance}
              entryToken={waitInQueue.entryToken}
            />
          )}
        </div>
      </div>
    </PageLayout>
  )
}
