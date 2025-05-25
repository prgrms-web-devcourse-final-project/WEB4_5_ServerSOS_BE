import { useQuery } from "@tanstack/react-query"
import {
  getLoginInfo,
  setLoginInfo,
  removeLoginInfo,
} from "../lib/storage/loginStorage"
import { apiClient } from "@/api/apiClient"
import type { LoginRequest } from "@/api/__generated__"
import { getCookie } from "@/lib/cookie"

async function login(loginRequest: LoginRequest) {
  const response = await apiClient.member.login({ loginRequest })

  if (response.code !== 200 || !response.data?.accessToken) {
    throw new Error("Failed to login")
  }

  setLoginInfo({
    token: response.data.accessToken,
  })
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

      const response = await apiClient.member.myInfo({
        headers: {
          Authorization: `Bearer ${loginInfo.token}`,
        },
      })

      if (response.code !== 200) {
        const refreshToken = getCookie("refreshToken")

        if (!refreshToken) {
          throw new Error("No refresh token")
        }

        const tokenResponse = await apiClient.token.renewToken({
          refreshToken,
        })

        if (tokenResponse.code !== 200 || !tokenResponse.data?.accessToken) {
          throw new Error("Failed to renew token")
        }

        setLoginInfo({
          token: tokenResponse.data.accessToken,
        })

        throw new Error("Failed to fetch user info")
      }

      return response.data
    },
    throwOnError(error, query) {
      removeLoginInfo()

      return false
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
    login,
    isLoading,
  }
}
