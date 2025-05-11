import { apiClient } from "@/api/apiClient"
import { useQuery } from "@tanstack/react-query"

export const usePostDetail = ({ id }: { id: number }) => {
  const { data, isLoading, error } = useQuery({
    queryKey: ["post", id],
    queryFn: () =>
      apiClient.post.getPost({
        id,
      }),
  })

  return {
    post: data?.data,
    isLoading,
    error,
  }
}
