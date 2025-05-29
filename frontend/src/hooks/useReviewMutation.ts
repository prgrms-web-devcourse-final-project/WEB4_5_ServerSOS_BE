import { apiClient } from "@/api/apiClient"
import { useMutation, useQueryClient } from "@tanstack/react-query"

export const useCreateReview = ({
  performanceId,
}: { performanceId: number }) => {
  const queryClient = useQueryClient()

  const { mutate: createReview, isPending } = useMutation({
    mutationFn: (req: {
      postId: number
      contents: string
    }) =>
      apiClient.review.createReview({
        id: req.postId,
        postReviewContentRequest: {
          content: req.contents,
        },
      }),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: ["reviews", performanceId],
      })
    },
  })

  return { createReview, isPending }
}
