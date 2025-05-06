import { useState } from "react"
import { ChevronLeft, ChevronRight } from "lucide-react"
import { Button } from "@/components/ui/button"

interface SimpleCalendarProps {
  onSelectDate: (date: Date) => void
  selectedDate?: Date
}

export default function SimpleCalendar({
  onSelectDate,
  selectedDate,
}: SimpleCalendarProps) {
  const [currentMonth, setCurrentMonth] = useState(new Date("2025-05-01"))

  // 현재 월의 첫 날과 마지막 날 계산
  const firstDayOfMonth = new Date(
    currentMonth.getFullYear(),
    currentMonth.getMonth(),
    1,
  )
  const lastDayOfMonth = new Date(
    currentMonth.getFullYear(),
    currentMonth.getMonth() + 1,
    0,
  )

  // 이전 달의 마지막 날들 계산 (달력 첫 주 채우기 위함)
  const daysFromPrevMonth = firstDayOfMonth.getDay()

  // 달력에 표시할 날짜 배열 생성
  const calendarDays: Date[] = []

  // 이전 달의 날짜들 추가
  const prevMonthLastDay = new Date(
    currentMonth.getFullYear(),
    currentMonth.getMonth(),
    0,
  ).getDate()
  for (let i = daysFromPrevMonth - 1; i >= 0; i--) {
    calendarDays.push(
      new Date(
        currentMonth.getFullYear(),
        currentMonth.getMonth() - 1,
        prevMonthLastDay - i,
      ),
    )
  }

  // 현재 달의 날짜들 추가
  for (let i = 1; i <= lastDayOfMonth.getDate(); i++) {
    calendarDays.push(
      new Date(currentMonth.getFullYear(), currentMonth.getMonth(), i),
    )
  }

  // 다음 달의 날짜들 추가 (달력 마지막 주 채우기 위함)
  const remainingDays = 42 - calendarDays.length // 6주 x 7일 = 42
  for (let i = 1; i <= remainingDays; i++) {
    calendarDays.push(
      new Date(currentMonth.getFullYear(), currentMonth.getMonth() + 1, i),
    )
  }

  // 이전 달로 이동
  const goToPreviousMonth = () => {
    setCurrentMonth(
      new Date(currentMonth.getFullYear(), currentMonth.getMonth() - 1, 1),
    )
  }

  // 다음 달로 이동
  const goToNextMonth = () => {
    setCurrentMonth(
      new Date(currentMonth.getFullYear(), currentMonth.getMonth() + 1, 1),
    )
  }

  // 날짜 선택 핸들러
  const handleDateClick = (date: Date) => {
    console.log("날짜 클릭됨:", date.toDateString())
    onSelectDate(date)
  }

  // 날짜가 선택되었는지 확인
  const isDateSelected = (date: Date) => {
    if (!selectedDate) return false

    return (
      date.getDate() === selectedDate.getDate() &&
      date.getMonth() === selectedDate.getMonth() &&
      date.getFullYear() === selectedDate.getFullYear()
    )
  }

  // 날짜가 현재 달에 속하는지 확인
  const isCurrentMonth = (date: Date) => {
    return date.getMonth() === currentMonth.getMonth()
  }

  // 요일 레이블
  const weekDays = ["일", "월", "화", "수", "목", "금", "토"]

  // 월 이름
  const monthNames = [
    "1월",
    "2월",
    "3월",
    "4월",
    "5월",
    "6월",
    "7월",
    "8월",
    "9월",
    "10월",
    "11월",
    "12월",
  ]

  return (
    <div className="w-full">
      {/* 캘린더 헤더 */}
      <div className="flex justify-between items-center mb-4">
        <Button
          variant="outline"
          size="icon"
          onClick={goToPreviousMonth}
          className="h-8 w-8"
        >
          <ChevronLeft className="h-4 w-4" />
        </Button>
        <h3 className="text-lg font-medium">
          {currentMonth.getFullYear()}년 {monthNames[currentMonth.getMonth()]}
        </h3>
        <Button
          variant="outline"
          size="icon"
          onClick={goToNextMonth}
          className="h-8 w-8"
        >
          <ChevronRight className="h-4 w-4" />
        </Button>
      </div>

      {/* 요일 헤더 */}
      <div className="grid grid-cols-7 gap-1 mb-2">
        {weekDays.map((day, index) => (
          <div
            key={index}
            className={`text-center text-sm font-medium py-1 ${index === 0 ? "text-red-500" : ""}`}
          >
            {day}
          </div>
        ))}
      </div>

      {/* 캘린더 그리드 */}
      <div className="grid grid-cols-7 gap-1">
        {calendarDays.map((date, index) => (
          <button
            key={index}
            type="button"
            onClick={() => handleDateClick(date)}
            className={`
              h-10 w-full flex items-center justify-center rounded-md text-sm
              ${!isCurrentMonth(date) ? "text-gray-300" : ""}
              ${isDateSelected(date) ? "bg-blue-600 text-white font-bold" : "hover:bg-gray-100"}
              cursor-pointer transition-colors duration-200
            `}
          >
            {date.getDate()}
          </button>
        ))}
      </div>
    </div>
  )
}
