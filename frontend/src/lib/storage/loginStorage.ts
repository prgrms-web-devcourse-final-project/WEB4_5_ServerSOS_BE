// 로그인 정보 타입 예시 (필요에 따라 수정)
export interface LoginInfo {
  token: string
}

const STORAGE_KEY = "loginInfo"

// 로그인 정보 저장
export function setLoginInfo(info: LoginInfo) {
  sessionStorage.setItem(STORAGE_KEY, JSON.stringify(info))
}

// 로그인 정보 가져오기
export function getLoginInfo(): LoginInfo | null {
  const data = sessionStorage.getItem(STORAGE_KEY)
  return data ? (JSON.parse(data) as LoginInfo) : null
}

// 로그인 정보 삭제
export function removeLoginInfo() {
  sessionStorage.removeItem(STORAGE_KEY)
}
