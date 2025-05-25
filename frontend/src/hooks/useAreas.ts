import { useQuery } from "@tanstack/react-query"
import { apiClient } from "@/api/apiClient"

export const useAreas = ({
  sessionId,
}: {
  sessionId: number
}) => {
  const { data, isLoading, error } = useQuery({
    queryKey: ["areas"],
    queryFn: () =>
      apiClient.area.getAreas({
        sessionId,
      }),
  })

  return {
    areas: data?.data,
    isLoading,
    error,
  }
}
