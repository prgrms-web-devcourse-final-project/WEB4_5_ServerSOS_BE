import { useUser } from "@/hooks/useUser"
import { PageLayout } from "@/layout/PageLayout"
import { apiClient } from "@/api/apiClient"
import { Eye, EyeOff } from "lucide-react"
import { useState } from "react"
import { useNavigate } from "react-router-dom"
import { removeLoginInfo } from "@/lib/storage/loginStorage"

export const EditProfile = () => {
  const navigate = useNavigate()
  const { user } = useUser()
  const [nickname, setNickname] = useState(user?.nickname || "")
  const [password, setPassword] = useState("")
  const [confirmPassword, setConfirmPassword] = useState("")
  const [showPassword, setShowPassword] = useState(false)
  const [showConfirmPassword, setShowConfirmPassword] = useState(false)
  const [loading, setLoading] = useState(false)

  if (!user) return null

  const handleUpdate = async (e: React.FormEvent) => {
    e.preventDefault()
    if (password && password !== confirmPassword) {
      alert("비밀번호가 일치하지 않습니다.")
      return
    }

    try {
      setLoading(true)

      // 닉네임 수정
      await apiClient.member.updateMyInfo({
        memberUpdateRequest: {
          nickname,
        },
      })

      // 비밀번호 변경 (입력된 경우에만)
      if (password) {
        await apiClient.member.updatePassword({
          memberPasswordUpdateRequest: {
            password,
          },
        })
      }

      alert("회원 정보가 수정되었습니다.")
      navigate("/my")
    } catch (err) {
      console.log(err)
      alert("정보 수정 중 오류가 발생했습니다.")
    } finally {
      setLoading(false)
    }
  }

  const handleDeleteAccount = async () => {
    if (
      !window.confirm(
        "정말로 회원 탈퇴하시겠습니까? 이 작업은 되돌릴 수 없습니다.",
      )
    )
      return
    try {
      await apiClient.member.signout()
      removeLoginInfo()
      alert("회원 탈퇴가 완료되었습니다.")
      window.location.href = "/"
    } catch (err) {
      alert("회원 탈퇴 중 오류가 발생했습니다.")
    }
  }

  return (
    <PageLayout>
      <div className="max-w-md mx-auto py-12">
        <h1 className="text-2xl font-bold mb-6">회원 정보 수정</h1>

        <form onSubmit={handleUpdate} className="space-y-6">
          {/* 닉네임 수정 */}
          <div>
            <label className="block text-sm mb-1">닉네임</label>
            <input
              type="text"
              value={nickname}
              onChange={(e) => setNickname(e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-md"
            />
          </div>

          {/* 비밀번호 변경 */}
          <div>
            <label className="block text-sm mb-1">새 비밀번호</label>
            <div className="relative">
              <input
                type={showPassword ? "text" : "password"}
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-md"
              />
              <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                className="absolute inset-y-0 right-0 pr-3 flex items-center"
              >
                {showPassword ? (
                  <EyeOff className="h-5 w-5" />
                ) : (
                  <Eye className="h-5 w-5" />
                )}
              </button>
            </div>
          </div>

          {/* 비밀번호 확인 */}
          <div>
            <label className="block text-sm mb-1">비밀번호 확인</label>
            <div className="relative">
              <input
                type={showConfirmPassword ? "text" : "password"}
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-md"
              />
              <button
                type="button"
                onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                className="absolute inset-y-0 right-0 pr-3 flex items-center"
              >
                {showConfirmPassword ? (
                  <EyeOff className="h-5 w-5" />
                ) : (
                  <Eye className="h-5 w-5" />
                )}
              </button>
            </div>
          </div>

          {/* 수정 버튼 */}
          <button
            type="submit"
            disabled={loading}
            className="w-full bg-black text-white py-3 rounded-md hover:bg-gray-800"
          >
            {loading ? "처리 중..." : "정보 수정"}
          </button>
        </form>

        {/* 회원 탈퇴 버튼 */}
        <div className="mt-6 text-center">
          <button
            onClick={handleDeleteAccount}
            className="text-sm text-red-600 hover:underline"
          >
            회원 탈퇴
          </button>
        </div>
      </div>
    </PageLayout>
  )
}
