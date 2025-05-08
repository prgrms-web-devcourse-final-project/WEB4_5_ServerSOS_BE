import { MemberAPIApi, Configuration } from "./__generated__"

export const BACKEND_API = "https://api.team2.pick-go.shop/"

// API 클라이언트 설정
const config = new Configuration({
  basePath: BACKEND_API, // "http://localhost:8080",
  credentials: "include",
})

// 모든 API 클라이언트를 하나의 객체로 통합
export const apiClient = {
  member: new MemberAPIApi(config),
}
