import { useQuery } from "@tanstack/react-query"
import { getLoginInfo } from "../lib/storage/loginStorage"

interface User {
  id: string
  email: string
  nickname: string
  authority: "USER" | "ADMIN" // 권한 타입을 리터럴 타입으로 정의
  profile: string
  socialProvider: "NONE" | "GOOGLE" | "KAKAO" // 소셜 로그인 제공자 타입
  createdAt: string
  modifiedAt: string
}

// API 호출 함수
async function fetchUserInfo(token: string): Promise<User> {
  const response = await fetch("/api/member", {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  })

  if (!response.ok) {
    throw new Error("Failed to fetch user info")
  }

  return response.json()
}

export function useUser() {
  const {
    data: user,
    isLoading,
    error,
  } = useQuery({
    queryKey: ["user"],
    queryFn: async () => {
      const loginInfo = getLoginInfo()

      if (!loginInfo?.token) {
        throw new Error("No auth token")
      }

      return fetchUserInfo(loginInfo.token)
    },
    // 로그인 정보가 없을 때는 쿼리를 실행하지 않음
    enabled: !!getLoginInfo()?.token,
  })

  const isLogin = !!user && !error
  const checkLogin = () => !!getLoginInfo()?.token

  return {
    isLogin,
    user,
    checkLogin,
    isLoading,
  }
}
