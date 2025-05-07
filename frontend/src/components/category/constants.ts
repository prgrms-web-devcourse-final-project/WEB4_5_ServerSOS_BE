export const CATEGORIES = [
  { id: "all", name: "전체" },
  { id: "musical", name: "뮤지컬" },
  { id: "concert", name: "콘서트" },
  { id: "sports", name: "스포츠" },
  { id: "exhibition", name: "전시/행사" },
  { id: "classic", name: "클래식/무용" },
  { id: "theater", name: "연극" },
  { id: "family", name: "가족/아동" },
  { id: "leisure", name: "레저/여행" },
  { id: "md", name: "MD샵" },
  { id: "promotion", name: "할인" },
  { id: "membership", name: "멤버십" },
  { id: "venue", name: "지역별" },
]

export const VALID_GENRES = CATEGORIES.map((category) => category.id)
