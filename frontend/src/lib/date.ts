import { formatDate } from "date-fns"

export const getDurationStr = (startDate?: Date, endDate?: Date) => {
  if (!startDate || !endDate) {
    return ""
  }

  return `${formatDate(startDate, "yyyy.MM.dd")} - ${formatDate(endDate, "yyyy.MM.dd")}`
}
