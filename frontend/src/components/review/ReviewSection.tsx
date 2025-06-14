import { useState } from "react"
import { Send } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Textarea } from "@/components/ui/textarea"

import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { useReviews } from "@/hooks/useReviews"
import { useCreateReview } from "@/hooks/useReviewMutation"

export default function ReviewSection({ showId }: { showId: number }) {
  const { reviews } = useReviews({ performanceId: showId })

  const { createReview, isPending } = useCreateReview({
    performanceId: showId,
  })
  const [newReview, setNewReview] = useState("")

  // 리뷰 작성 처리
  const handleSubmitReview = () => {
    if (!newReview.trim()) return

    createReview({
      postId: showId,
      contents: newReview,
    })

    setNewReview("")
  }

  return (
    <div className="space-y-6">
      {/* 리뷰 작성 폼 */}
      <div className="flex items-start gap-4 pb-6 border-b">
        <Avatar className="w-10 h-10">
          <AvatarImage src="/placeholder.svg?key=zz952" alt="내 프로필" />
          <AvatarFallback>ME</AvatarFallback>
        </Avatar>
        <div className="flex-1">
          <Textarea
            placeholder="댓글 추가"
            value={newReview}
            onChange={(e) => setNewReview(e.target.value)}
            className="resize-none mb-2"
            rows={3}
          />
          <div className="flex justify-end">
            <Button
              onClick={handleSubmitReview}
              disabled={!newReview.trim() || isPending}
              size="sm"
              className="gap-1"
            >
              <Send className="h-4 w-4" />
              등록
            </Button>
          </div>
        </div>
      </div>

      {/* 리뷰 목록 */}
      <div>
        <h3 className="text-lg font-medium mb-4">리뷰 {reviews.length}개</h3>
        <ul className="space-y-6">
          {reviews.map((review) => (
            <li key={review.reviewId} className="flex gap-4">
              <Avatar className="w-10 h-10">
                <AvatarImage
                  src={review.profile || "/placeholder.svg"}
                  alt={review.nickname}
                />
                <AvatarFallback>
                  {review.nickname?.substring(1, 3).toUpperCase()}
                </AvatarFallback>
              </Avatar>
              <div className="flex-1">
                <div className="flex justify-between items-start">
                  <div>
                    <p className="font-medium">{review.nickname}</p>
                    <p className="mt-1">{review.content}</p>
                  </div>
                </div>
              </div>
            </li>
          ))}
        </ul>
      </div>
    </div>
  )
}
