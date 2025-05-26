import { useQuery } from "@tanstack/react-query"
import { apiClient } from "@/api/apiClient"

export const useAreas = ({
  sessionId,
  entryToken,
}: {
  sessionId?: number
  entryToken?: string
}) => {
  const { data, isLoading, error } = useQuery({
    queryKey: ["areas"],
    queryFn: () => {
      if (!sessionId) {
        return {
          data: [],
        }
      }

      return apiClient.area.getAreas(
        {
          sessionId,
        },
        {
          headers: {
            EntryAuth: `Bearer ${entryToken}`,
          },
        },
      )
    },
  })

  return {
    areas: data?.data,
    isLoading,
    error,
  }
}
