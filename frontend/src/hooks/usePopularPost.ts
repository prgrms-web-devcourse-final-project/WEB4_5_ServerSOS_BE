import type { GetPopularPostsRequest } from "@/api/__generated__"
import { apiClient } from "@/api/apiClient"
import { useQuery } from "@tanstack/react-query"

export const usePopularPost = ({ size, type }: GetPopularPostsRequest) => {
  const { data, isLoading, error } = useQuery({
    queryKey: ["popularPost", size, type],
    queryFn: () => apiClient.post.getPopularPosts({ size, type }),
  })

  return {
    popularPosts: data?.data,
    isLoading,
    error,
  }
}
