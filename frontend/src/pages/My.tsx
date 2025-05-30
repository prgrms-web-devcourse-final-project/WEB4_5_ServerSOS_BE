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

      {/* 메뉴 항목 */}
      <div className="py-10">
        <ul className="border-t">
          <li className="p-6 border-b">
            <Link
              // to="/mypage/bookings"
              to="/my/reservation"
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
        </ul>
      </div>
    </PageLayout>
  )
}
