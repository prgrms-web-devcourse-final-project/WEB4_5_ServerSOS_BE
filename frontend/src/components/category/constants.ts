import type { GetPopularPostsTypeEnum } from "@/api/__generated__"

export type CATEGORY_TYPES = GetPopularPostsTypeEnum | "ALL"

export const CATEGORIES: { id: GetPopularPostsTypeEnum; name: string }[] = [
  { id: "MUSICAL", name: "뮤지컬" },
  { id: "CONCERT", name: "콘서트" },
  { id: "PLAY", name: "연극" },
  { id: "CLASSIC", name: "클래식/무용" },
  { id: "DANCE", name: "무용" },
  { id: "KOREAN", name: "국악" },
  { id: "ETC", name: "기타" },
]

export const ALL_CATEGORY = { id: "ALL", name: "전체" }
export const ALL_CATEGORIES = [ALL_CATEGORY, ...CATEGORIES]
