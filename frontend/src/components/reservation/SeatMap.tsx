import { useRef, useState, useEffect } from "react"
import { cn } from "@/lib/utils"

// 좌석 데이터 타입 정의
interface Seat {
  id: string
  section: string
  row: number
  number: number
  x: number
  y: number
  status: "available" | "reserved" | "selected" | "sold"
}

interface SeatMapProps {
  selectedSection: string | null
  selectedSeats: string[]
  onSectionSelect: (section: string) => void
  onSeatSelect: (seatId: string) => void
  scale: number
}

export default function SeatMap({
  selectedSection,
  selectedSeats,
  onSectionSelect,
  onSeatSelect,
  scale,
}: SeatMapProps) {
  const containerRef = useRef<HTMLDivElement>(null)
  const [seats, setSeats] = useState<Seat[]>([])
  const [isDragging, setIsDragging] = useState(false)
  const [dragStart, setDragStart] = useState({ x: 0, y: 0 })
  const [position, setPosition] = useState({ x: 0, y: 0 })
  const [initialPosition, setInitialPosition] = useState({ x: 0, y: 0 })

  // 좌석 데이터 생성
  useEffect(() => {
    const generatedSeats: Seat[] = []

    // 섹션별 좌석 생성
    const sections = [
      {
        name: "R",
        rows: 2,
        seatsPerRow: 12,
        radius: 100,
        startAngle: -90,
        endAngle: 90,
      },
      {
        name: "S",
        rows: 2,
        seatsPerRow: 16,
        radius: 150,
        startAngle: -100,
        endAngle: 100,
      },
      {
        name: "A",
        rows: 2,
        seatsPerRow: 20,
        radius: 200,
        startAngle: -110,
        endAngle: 110,
      },
      {
        name: "B",
        rows: 2,
        seatsPerRow: 24,
        radius: 250,
        startAngle: -120,
        endAngle: 120,
      },
    ]

    sections.forEach((section) => {
      for (let row = 1; row <= section.rows; row++) {
        const currentRadius = section.radius + (row - 1) * 30
        const seatsInRow = section.seatsPerRow + (row - 1) * 4

        for (let i = 0; i < seatsInRow; i++) {
          const angle =
            section.startAngle +
            (section.endAngle - section.startAngle) * (i / (seatsInRow - 1))
          const radians = (angle * Math.PI) / 180

          const x = 400 + currentRadius * Math.cos(radians)
          const y = 300 + currentRadius * Math.sin(radians)

          // 랜덤하게 일부 좌석은 이미 판매된 상태로 설정
          const status = Math.random() > 0.9 ? "sold" : "available"

          generatedSeats.push({
            id: `${section.name}-${row}-${i + 1}`,
            section: section.name,
            row,
            number: i + 1,
            x,
            y,
            status,
          })
        }
      }
    })

    setSeats(generatedSeats)
  }, [])

  // 드래그 시작 핸들러
  const handleMouseDown = (e: React.MouseEvent) => {
    setIsDragging(true)
    setDragStart({ x: e.clientX, y: e.clientY })
    setInitialPosition({ ...position })
  }

  // 드래그 중 핸들러
  const handleMouseMove = (e: React.MouseEvent) => {
    if (!isDragging) return

    const dx = e.clientX - dragStart.x
    const dy = e.clientY - dragStart.y

    setPosition({
      x: initialPosition.x + dx / scale,
      y: initialPosition.y + dy / scale,
    })
  }

  // 드래그 종료 핸들러
  const handleMouseUp = () => {
    setIsDragging(false)
  }

  // 좌석 클릭 핸들러
  const handleSeatClick = (seat: Seat) => {
    if (seat.status === "sold") return

    onSeatSelect(seat.id)
  }

  // 섹션 클릭 핸들러
  const handleSectionClick = (section: string) => {
    onSectionSelect(section)
  }

  return (
    <div
      ref={containerRef}
      className="relative w-full h-full overflow-hidden cursor-move"
      onMouseDown={handleMouseDown}
      onMouseMove={handleMouseMove}
      onMouseUp={handleMouseUp}
      onMouseLeave={handleMouseUp}
    >
      <div
        className="absolute"
        style={{
          transform: `translate(${position.x}px, ${position.y}px) scale(${scale})`,
          transformOrigin: "center",
          transition: isDragging ? "none" : "transform 0.3s ease",
        }}
      >
        <svg width="800" height="600" viewBox="0 0 800 600">
          {/* 무대 */}
          <rect x="300" y="50" width="200" height="80" rx="10" fill="#6b7280" />
          <text
            x="400"
            y="95"
            textAnchor="middle"
            fill="white"
            fontSize="16"
            fontWeight="bold"
          >
            STAGE
          </text>

          {/* 섹션 영역 */}
          <g className="sections">
            <path
              d="M250,300 A150,150 0 0,1 550,300"
              fill="none"
              stroke={selectedSection === "R" ? "#f43f5e" : "#d1d5db"}
              strokeWidth="40"
              strokeOpacity="0.2"
              onClick={() => handleSectionClick("R")}
              className="cursor-pointer"
            />
            <path
              d="M200,300 A200,200 0 0,1 600,300"
              fill="none"
              stroke={selectedSection === "S" ? "#8b5cf6" : "#d1d5db"}
              strokeWidth="40"
              strokeOpacity="0.2"
              onClick={() => handleSectionClick("S")}
              className="cursor-pointer"
            />
            <path
              d="M150,300 A250,250 0 0,1 650,300"
              fill="none"
              stroke={selectedSection === "A" ? "#3b82f6" : "#d1d5db"}
              strokeWidth="40"
              strokeOpacity="0.2"
              onClick={() => handleSectionClick("A")}
              className="cursor-pointer"
            />
            <path
              d="M100,300 A300,300 0 0,1 700,300"
              fill="none"
              stroke={selectedSection === "B" ? "#10b981" : "#d1d5db"}
              strokeWidth="40"
              strokeOpacity="0.2"
              onClick={() => handleSectionClick("B")}
              className="cursor-pointer"
            />
          </g>

          {/* 좌석 */}
          <g className="seats">
            {seats.map((seat) => {
              // 선택된 섹션이 있고, 현재 좌석이 해당 섹션이 아니면 흐리게 표시
              const isFiltered =
                selectedSection !== null && seat.section !== selectedSection

              // 좌석 상태에 따른 색상 설정
              let fillColor = "#d1d5db" // 기본 색상

              if (seat.status === "sold") {
                fillColor = "#6b7280" // 판매됨
              } else if (selectedSeats.includes(seat.id)) {
                fillColor = "#f43f5e" // 선택됨
              } else if (seat.section === "R") {
                fillColor = "#fda4af" // R석
              } else if (seat.section === "S") {
                fillColor = "#c4b5fd" // S석
              } else if (seat.section === "A") {
                fillColor = "#93c5fd" // A석
              } else if (seat.section === "B") {
                fillColor = "#6ee7b7" // B석
              }

              return (
                <circle
                  key={seat.id}
                  cx={seat.x}
                  cy={seat.y}
                  r={10}
                  fill={fillColor}
                  opacity={isFiltered ? 0.3 : 1}
                  stroke={selectedSeats.includes(seat.id) ? "#f43f5e" : "white"}
                  strokeWidth={selectedSeats.includes(seat.id) ? 2 : 1}
                  className={cn(
                    "cursor-pointer transition-all duration-200",
                    seat.status === "sold"
                      ? "cursor-not-allowed"
                      : "hover:stroke-2",
                  )}
                  onClick={() => handleSeatClick(seat)}
                />
              )
            })}
          </g>

          {/* 섹션 레이블 */}
          <text
            x="400"
            y="220"
            textAnchor="middle"
            fill="#f43f5e"
            fontSize="14"
            fontWeight="bold"
          >
            R석
          </text>
          <text
            x="400"
            y="170"
            textAnchor="middle"
            fill="#8b5cf6"
            fontSize="14"
            fontWeight="bold"
          >
            S석
          </text>
          <text
            x="400"
            y="120"
            textAnchor="middle"
            fill="#3b82f6"
            fontSize="14"
            fontWeight="bold"
          >
            A석
          </text>
          <text
            x="400"
            y="70"
            textAnchor="middle"
            fill="#10b981"
            fontSize="14"
            fontWeight="bold"
          >
            B석
          </text>
        </svg>
      </div>

      <div className="absolute bottom-4 left-4 bg-white/80 p-2 rounded-md text-sm">
        드래그하여 이동, 확대/축소 버튼으로 크기 조절
      </div>
    </div>
  )
}
