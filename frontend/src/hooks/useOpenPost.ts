import { apiClient } from "@/api/apiClient"
import { useQuery } from "@tanstack/react-query"

export const useOpenPost = () => {
  const {
    data: openPost,
    isLoading,
    error,
  } = useQuery({
    queryKey: ["openPost"],
    queryFn: () => {
      return apiClient.post.getPopularPosts1()
    },
  })

  return { openPost: openPost?.data, isLoading, error }
}
