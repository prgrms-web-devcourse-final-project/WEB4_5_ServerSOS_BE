import { apiClient } from "@/api/apiClient"
import { useInfiniteQuery } from "@tanstack/react-query"

export const useMyReservationList = () => {
  const {
    data,
    isLoading,
    error,
    fetchNextPage,
    hasNextPage,
    isFetchingNextPage,
  } = useInfiniteQuery({
    queryKey: ["myReservationList"],
    queryFn: ({ pageParam = 1 }) =>
      apiClient.reservation.getMyReservationList({
        page: pageParam,
        size: 10,
      }),
    getNextPageParam: (lastPage, allPages) => {
      const currentPage = lastPage.data?.page ?? 1
      const totalPages = lastPage.data?.totalPages ?? 1
      return currentPage < totalPages ? currentPage + 1 : undefined
    },
    initialPageParam: 1,
  })

  // 모든 페이지의 데이터를 평면화
  const allReservations =
    data?.pages.flatMap((page) => page.data?.items ?? []) ?? []

  return {
    myReservationList: allReservations,
    isLoading,
    error,
    fetchNextPage,
    hasNextPage,
    isFetchingNextPage,
  }
}
