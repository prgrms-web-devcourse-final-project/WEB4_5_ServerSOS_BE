import { useUser } from "@/hooks/useUser"
import { PageLayout } from "../layout/PageLayout"
import { formatDate } from "date-fns"
import { Link } from "react-router-dom"

export const My = () => {
  const { user } = useUser()

  if (!user) {
    return null
  }

  return (
    <PageLayout>
      <div className="flex flex-col md:flex-row gap-6 p-10">
        <div className="flex-shrink-0">
          <div className="w-32 h-32 rounded-full overflow-hidden ">
            <img
              src={
                user.profile ||
                "/placeholder.svg?height=200&width=200&query=profile"
              }
              alt="프로필 이미지"
              width={128}
              height={128}
              className="object-cover w-full h-full"
            />
          </div>
        </div>

        <div className="flex-grow">
          <div className="p-4 rounded-md">
            <div className="grid grid-cols-2 gap-4">
              <div>
                <p className="text-gray-600 text-sm">사용자명</p>
                <p className="font-medium">{user.nickname}</p>
              </div>
              <div>
                <p className="text-gray-600 text-sm">이메일</p>
                <p className="font-medium">{user.email}</p>
              </div>
              <div>
                <p className="text-gray-600 text-sm">가입일</p>
                <p className="font-medium">
                  {user.createdAt
                    ? formatDate(user.createdAt, "yyyy-MM-dd")
                    : "정보 없음"}
                </p>
              </div>
              <div>
                <p className="text-gray-600 text-sm">계정 유형</p>
                <p className="font-medium">
                  {user.socialProvider === "NONE"
                    ? "일반 회원"
                    : user.socialProvider}
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* 내 활동 */}
      <div className="p-10">
        <h2 className="text-lg font-medium mb-4">내 활동</h2>
        <div className="p-6 rounded-md">
          <div className="grid grid-cols-2 md:grid-cols-4 gap-6 text-center">
            <div className="flex flex-col items-center">
              <div className="w-16 h-16 bg-[#fbd57e] flex items-center justify-center rounded-md transform rotate-45 mb-3">
                <span className="transform -rotate-45 font-bold">CNT</span>
              </div>
              <p>관람</p>
            </div>
            <div className="flex flex-col items-center">
              <div className="w-16 h-16 bg-[#798ba9] flex items-center justify-center rounded-md transform rotate-45 mb-3">
                <span className="transform -rotate-45 font-bold text-white">
                  CNT
                </span>
              </div>
              <p>리뷰</p>
            </div>
            <div className="flex flex-col items-center">
              <div className="w-16 h-16 bg-[#d9d9d9] flex items-center justify-center rounded-md transform rotate-45 mb-3">
                <span className="transform -rotate-45 font-bold">CNT</span>
              </div>
              <p>찜</p>
            </div>
            <div className="flex flex-col items-center">
              <div className="w-16 h-16 bg-[#d9d9d9] flex items-center justify-center rounded-md transform rotate-45 mb-3">
                <span className="transform -rotate-45 font-bold">CNT</span>
              </div>
              <p>문의</p>
            </div>
          </div>
        </div>
      </div>

      {/* 메뉴 항목 */}
      <div className="py-10">
        <ul className="border-t">
          <li className="p-6 border-b">
            <Link
              // to="/mypage/bookings"
              to="./#"
              className="flex justify-between items-center hover:text-blue-600"
            >
              <span>예매/취소내역</span>
              <span>›</span>
            </Link>
          </li>
          <li className="p-6 border-b">
            <Link
              // to="/mypage/edit-profile"
              to="./#"
              className="flex justify-between items-center hover:text-blue-600"
            >
              <span>내 정보 수정</span>
              <span>›</span>
            </Link>
          </li>
          <li className="p-6 border-b">
            <Link
              // to="/mypage/notifications"
              to="./#"
              className="flex justify-between items-center hover:text-blue-600"
            >
              <span>알림 설정</span>
              <span>›</span>
            </Link>
          </li>
          <li className="p-6">
            <Link
              // to="/mypage/inquiries"
              to="./#"
              className="flex justify-between items-center hover:text-blue-600"
            >
              <span>문의 내역</span>
              <span>›</span>
            </Link>
          </li>
        </ul>
      </div>
    </PageLayout>
  )
}
