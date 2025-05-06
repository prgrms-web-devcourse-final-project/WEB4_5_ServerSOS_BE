import { useState, useEffect } from "react"
import { MoreVertical, Send } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Textarea } from "@/components/ui/textarea"
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"

interface Review {
  id: number
  userId: string
  username: string
  profileImage: string
  content: string
  createdAt: string
  isMyReview: boolean
}

export default function ReviewSection({ showId }: { showId: number }) {
  const [reviews, setReviews] = useState<Review[]>([])
  const [newReview, setNewReview] = useState("")
  const [isSubmitting, setIsSubmitting] = useState(false)

  // 리뷰 데이터 로드
  useEffect(() => {
    // 실제 구현에서는 API 호출로 대체
    const mockReviews: Review[] = [
      {
        id: 1,
        userId: "lkjfdasdf",
        username: "@lkjfdasdf",
        profileImage: "/diverse-professional-profiles.png",
        content: "진짜 재밌게 잘 봤습니다!",
        createdAt: "2025-04-28T14:30:45",
        isMyReview: true,
      },
      {
        id: 2,
        userId: "kasdgkihlk",
        username: "@kasdgkihlk",
        profileImage: "/abstract-user-profile.png",
        content: "적극 추천👍👍",
        createdAt: "2025-04-27T10:15:22",
        isMyReview: false,
      },
      {
        id: 3,
        userId: "akshfkjwqe",
        username: "@akshfkjwqe",
        profileImage: "/abstract-geometric-profile.png",
        content: "시간 가는줄 몰랐네요~",
        createdAt: "2025-04-26T18:45:10",
        isMyReview: false,
      },
      {
        id: 4,
        userId: "sadkjhzxc",
        username: "@sadkjhzxc",
        profileImage: "/abstract-geometric-profile.png",
        content: "대박👏",
        createdAt: "2025-04-25T09:20:33",
        isMyReview: false,
      },
    ]

    setReviews(mockReviews)
  }, [])

  // 리뷰 작성 처리
  const handleSubmitReview = () => {
    if (!newReview.trim()) return

    setIsSubmitting(true)

    // 실제 구현에서는 API 호출로 대체
    setTimeout(() => {
      const newReviewObj: Review = {
        id: Date.now(),
        userId: "currentUser",
        username: "@currentUser",
        profileImage: "/placeholder.svg?key=90r20",
        content: newReview,
        createdAt: new Date().toISOString(),
        isMyReview: true,
      }

      setReviews([newReviewObj, ...reviews])
      setNewReview("")
      setIsSubmitting(false)
    }, 500)
  }

  // 리뷰 삭제 처리
  const handleDeleteReview = (reviewId: number) => {
    // 실제 구현에서는 API 호출로 대체
    setReviews(reviews.filter((review) => review.id !== reviewId))
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
              disabled={!newReview.trim() || isSubmitting}
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
            <li key={review.id} className="flex gap-4">
              <Avatar className="w-10 h-10">
                <AvatarImage
                  src={review.profileImage || "/placeholder.svg"}
                  alt={review.username}
                />
                <AvatarFallback>
                  {review.username.substring(1, 3).toUpperCase()}
                </AvatarFallback>
              </Avatar>
              <div className="flex-1">
                <div className="flex justify-between items-start">
                  <div>
                    <p className="font-medium">{review.username}</p>
                    <p className="mt-1">{review.content}</p>
                  </div>
                  {review.isMyReview && (
                    <DropdownMenu>
                      <DropdownMenuTrigger asChild>
                        <Button variant="ghost" size="icon" className="h-8 w-8">
                          <MoreVertical className="h-4 w-4" />
                          <span className="sr-only">메뉴 열기</span>
                        </Button>
                      </DropdownMenuTrigger>
                      <DropdownMenuContent align="end">
                        <DropdownMenuItem
                          onClick={() => handleDeleteReview(review.id)}
                          className="text-red-500"
                        >
                          삭제하기
                        </DropdownMenuItem>
                      </DropdownMenuContent>
                    </DropdownMenu>
                  )}
                </div>
              </div>
            </li>
          ))}
        </ul>
      </div>
    </div>
  )
}
