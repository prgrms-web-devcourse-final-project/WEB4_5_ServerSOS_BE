import { useUser } from "@/hooks/useUser"
import { PageLayout } from "../layout/PageLayout"
import { formatDate } from "date-fns"
import { Link } from "react-router-dom"
import { useRef, useState } from "react"
import { apiClient } from "@/api/apiClient"

export const My = () => {
  const { user, refetch } = useUser()
  const fileInputRef = useRef<HTMLInputElement>(null)
  const [uploading, setUploading] = useState(false)

  if (!user) {
    return null
  }

  const handleFileChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0]
    if (!file) return

    try {
      setUploading(true)
      await apiClient.member.updateProfileImage({ image: file })
      await refetch?.()
    } catch (err) {
      console.error("프로필 이미지 업로드 실패:", err)
      alert("이미지 업로드에 실패했습니다.")
    } finally {
      setUploading(false)
    }
  }

  const triggerFileInput = () => {
    fileInputRef.current?.click()
  }

  return (
    <PageLayout>
      <div className="flex flex-col md:flex-row gap-6 p-10">
        <div className="flex-shrink-0 flex flex-col items-center">
          <div className="w-32 h-32 rounded-full overflow-hidden">
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
          <input
            ref={fileInputRef}
            type="file"
            accept="image/*"
            className="hidden"
            onChange={handleFileChange}
          />
          <button
            onClick={triggerFileInput}
            className="mt-2  px-3 py-1 text-sm rounded bg-black text-white disabled:opacity-50"
            disabled={uploading}
          >
            {uploading ? "업로드 중..." : "변경"}
          </button>
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
              to="/my/edit-profile"
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
