import { useMutation } from "@tanstack/react-query"
import { apiClient } from "@/api/apiClient"
import type { SeatRequest } from "@/api/__generated__"

export const useCreateReservation = () => {
  const { mutateAsync: reserveSeats } = useMutation({
    mutationFn: async ({
      sessionId,
      seats,
      entryToken,
    }: { sessionId: number; seats: SeatRequest[]; entryToken: string }) => {
      const response = await apiClient.reservation.createReservation(
        {
          reservationCreateRequest: {
            performanceSessionId: sessionId,
            seats,
          },
        },
        {
          headers: {
            EntryAuth: `Bearer ${entryToken}`,
          },
        },
      )

      return response.data
    },
  })

  return {
    reserveSeats,
  }
}
