import { useQuery, useQueryClient } from "@tanstack/react-query"
import {
  getLoginInfo,
  setLoginInfo,
  removeLoginInfo,
} from "../lib/storage/loginStorage"
import { apiClient } from "@/api/apiClient"
import type { LoginRequest } from "@/api/__generated__"
import { getCookie } from "@/lib/cookie"
import { useNavigate } from "react-router-dom"

async function login(loginRequest: LoginRequest) {
  const response = await apiClient.member.login({ loginRequest })

  if (response.code !== 200 || !response.data?.accessToken) {
    throw new Error("Failed to login")
  }

  setLoginInfo({
    token: response.data.accessToken,
  })
}

const USER_QUERY_KEY = ["user"]

export function useUser() {
  const navigate = useNavigate()
  const queryClient = useQueryClient()

  const {
    data: user,
    isLoading,
    error,
  } = useQuery({
    queryKey: USER_QUERY_KEY,
    queryFn: async () => {
      const loginInfo = getLoginInfo()
      const accessToken = loginInfo?.token
      let response = null

      if (accessToken) {
        response = await fetchMyInfo(accessToken)
      }

      if (!accessToken || response.code !== 200) {
        const tokenResponse = await apiClient.token.renewToken({
          refreshToken: "dummy",
        })

        if (tokenResponse.code !== 201 || !tokenResponse.data?.accessToken) {
          throw new Error("Failed to renew token")
        }

        const newAccessToken = tokenResponse.data.accessToken

        setLoginInfo({
          token: newAccessToken,
        })

        response = await fetchMyInfo(newAccessToken)
      }

      return response.data
    },
    throwOnError: false,
  })

  const isLogin = !!user && !error
  const checkLogin = () => !!getLoginInfo()?.token

  const logout = async () => {
    const loginInfo = getLoginInfo()

    try {
      if (loginInfo?.token) {
        // API 요청: 로그아웃
        await apiClient.member.logout()
      }
    } catch (e) {
      console.error("Logout API 호출 중 오류:", e)
      // API 실패하더라도 클라이언트 로그아웃은 계속 진행
    }

    removeLoginInfo()
    queryClient.removeQueries({ queryKey: USER_QUERY_KEY })
    navigate("/")
  }

  return {
    isLogin,
    user,
    checkLogin,
    login,
    logout,
    isLoading,
  }
}

async function fetchMyInfo(token: string) {
  return apiClient.member.myInfo({
    headers: {
      Authorization: `Bearer ${token}`,
    },
  })
}
