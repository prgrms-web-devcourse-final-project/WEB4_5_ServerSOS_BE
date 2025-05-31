import type { GetPostsSortEnum } from "@/api/__generated__"
import { apiClient } from "@/api/apiClient"
import type { CATEGORY_TYPES } from "@/components/category/constants"
import { useQuery } from "@tanstack/react-query"

const DEFAULT_SIZE = 20

export const usePosts = ({
  keyword = "",
  type,
  sort,
  page = 0,
}: {
  type: CATEGORY_TYPES
  sort: GetPostsSortEnum
  page?: number
}) => {
  const { data, isLoading, error } = useQuery({
    queryKey: ["post", type, sort, page, keyword],
    queryFn: () => {
      return apiClient.post.getPosts({
        size: DEFAULT_SIZE,
        page: page,
        type: type === "ALL" ? undefined : type,
        sort,
        keyword,
      })
    },
  })

  return {
    posts: data?.data?.items,
    totalPages: data?.data?.totalPages ?? 1,
    isLoading,
    error,
  }
}
