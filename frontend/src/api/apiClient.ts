import { getLoginInfo } from "@/lib/storage/loginStorage"
import {
  MemberAPIApi,
  Configuration,
  PostControllerApi,
  TokenAPIApi,
  AreaAPIApi,
} from "./__generated__"

export const BACKEND_API = "https://api.team2.pick-go.shop"

const customFetch = async (url: string, init?: RequestInit) => {
  const loginInfo = getLoginInfo()

  // 기존 헤더와 새로운 Authorization 헤더를 병합
  const headers = {
    ...init?.headers,
    ...(loginInfo?.token
      ? {
          Authorization: `Bearer ${loginInfo.token}`,
        }
      : {}),
  }

  // 새로운 init 객체 생성
  const newInit = {
    ...init,
    headers,
  }

  // 원래의 fetch 함수 호출
  return fetch(url, newInit)
}

// API 클라이언트 설정
const config = new Configuration({
  basePath: BACKEND_API, // "http://localhost:8080",
  credentials: "include",
  fetchApi: customFetch,
})

// 모든 API 클라이언트를 하나의 객체로 통합
export const apiClient = {
  member: new MemberAPIApi(config),
  post: new PostControllerApi(config),
  token: new TokenAPIApi(config),
  area: new AreaAPIApi(config),
}
