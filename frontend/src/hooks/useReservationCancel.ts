import { apiClient } from "@/api/apiClient"
import { QueryClient, useMutation, useQueryClient } from "@tanstack/react-query"

export const useReservationCancel = () => {
  const queryClient = useQueryClient()

  const { mutate: cancelReservation, isPending } = useMutation({
    mutationFn: (reservationId: number) =>
      apiClient.reservation.cancelReservation({
        id: reservationId,
      }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["myReservationList"] })
    },
  })

  return { cancelReservation, isPending }
}
