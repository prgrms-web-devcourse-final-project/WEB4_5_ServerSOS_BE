import { useEffect, useRef } from "react"
import { BACKEND_API } from "@/api/apiClient"
import { subscribeSSE } from "@/lib/subscribeSSE"
import { getLoginInfo } from "@/lib/storage/loginStorage"

type UseSubscriptionEnterQueueProps<T> = {
  sessionId?: number
  disabled?: boolean
  onMessage: (data: T, cleanup: () => void) => void
  onError?: (error: Event) => void
}

export function useSubscriptionEnterQueue<T>({
  sessionId,
  disabled = false,
  onMessage,
  onError,
}: UseSubscriptionEnterQueueProps<T>) {
  const abortControllerRef = useRef<AbortController | null>(null)

  useEffect(() => {
    if (disabled || !sessionId) return

    const loginInfo = getLoginInfo()

    if (!loginInfo) {
      throw new Error("No login info")
    }

    // 새로운 AbortController 생성
    abortControllerRef.current = new AbortController()

    subscribeSSE({
      url: `${BACKEND_API}/api/queue/stream?sessionId=${sessionId}`,
      onMessage: (data: T) => {
        const cleanup = () => {
          if (abortControllerRef.current) {
            abortControllerRef.current.abort()
            abortControllerRef.current = null
          }
        }

        onMessage(data, cleanup)
      },
      onError: (error) => {
        if (onError) {
          onError(error)
        }
      },
      token: loginInfo.token,
      signal: abortControllerRef.current.signal,
    })

    // cleanup 함수
    return () => {
      if (abortControllerRef.current) {
        abortControllerRef.current.abort()
        abortControllerRef.current = null
      }
    }
  }, [disabled, onMessage, onError, sessionId])
}
