import { apiClient } from "@/api/apiClient"
import { useQuery } from "@tanstack/react-query"

export const useReviews = ({
  performanceId,
}: {
  performanceId: number
}) => {
  const { data, isLoading, error } = useQuery({
    queryKey: ["reviews", performanceId],
    queryFn: () =>
      apiClient.review.getReviews({
        id: performanceId,
      }),
  })

  return {
    reviews: data?.data ?? [],
    isLoading,
    error,
  }
}
