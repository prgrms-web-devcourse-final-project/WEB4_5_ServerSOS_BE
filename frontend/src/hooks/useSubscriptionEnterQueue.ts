// src/hooks/useSubscriptionEnterQueue.ts
import { useEffect, useRef } from "react"
import { BACKEND_API } from "@/api/apiClient"
import { subscribeSSE } from "@/lib/subscribeSSE"
import { getLoginInfo } from "@/lib/storage/loginStorage"

type UseSubscriptionEnterQueueProps<T> = {
  disabled?: boolean
  onMessage: (data: T) => void
  onError?: (error: Event) => void
}

export function useSubscriptionEnterQueue<T>({
  disabled = false,
  onMessage,
  onError,
}: UseSubscriptionEnterQueueProps<T>) {
  useEffect(() => {
    if (disabled) return

    const loginInfo = getLoginInfo()

    if (!loginInfo) {
      throw new Error("No login info")
    }

    subscribeSSE({
      url: `${BACKEND_API}/api/queue/stream`,
      onMessage,
      onError: (error) => {
        if (onError) {
          onError(error)
        }
      },
      token: loginInfo.token,
    })
  }, [disabled, onMessage, onError])
}
