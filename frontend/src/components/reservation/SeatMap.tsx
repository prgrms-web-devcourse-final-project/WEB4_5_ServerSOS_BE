import type React from "react"
import {useRef, useState} from "react"
import {useNavigate} from "react-router-dom"
import {Button} from "@/components/ui/button"
import {Dialog, DialogContent, DialogHeader, DialogTitle,} from "@/components/ui/dialog"
import {toast} from "@/components/ui/use-toast"
import {cn} from "@/lib/utils"
import {Minus, Plus, Ticket, ZoomIn} from "lucide-react"
import {Slider} from "@/components/ui/slider"
import {useAreas} from "@/hooks/useAreas"
import {useCreateReservation} from "@/hooks/useReservation"
import type {
    PerformanceAreaDetailResponse,
    PerformanceDetailResponse,
    PerformanceSessionResponse,
} from "@/api/__generated__"

// 좌석 영역 정의
const SECTIONS = {
    P: {rows: 5, cols: 20, name: "P석", price: 150000, color: "#FEF3C7"},
    R: {rows: 15, cols: 10, name: "R석", price: 120000, color: "#DBEAFE"},
    S_LEFT: {
        rows: 15,
        cols: 10,
        name: "S석 (좌)",
        price: 100000,
        color: "#D1FAE5",
    },
    S_RIGHT: {
        rows: 15,
        cols: 10,
        name: "S석 (우)",
        price: 100000,
        color: "#D1FAE5",
    },
    A: {rows: 15, cols: 30, name: "A석", price: 80000, color: "#FEE2E2"},
}

type Seat = {
    areaId?: number
    status: "available" | "reserved"
    row: number
    col: number
}

type Seats = Seat[][]

// 좌석 상태 초기화 함수
const initializeSeats = (area: PerformanceAreaDetailResponse): Seats => {
    const rows = area.rowCount ?? 0
    const cols = area.colCount ?? 0

    const initSeats: Seats = Array(rows)
        .fill(null)
        .map((_, row) =>
            Array(cols)
                .fill(null)
                .map((_, col) => ({
                    areaId: area.id,
                    status: "available",
                    row: row,
                    col: col,
                })),
        )

    area.reservedSeats?.forEach((reservedSeat) => {
        console.log(reservedSeat)
        const row = convertRowToNumber(reservedSeat.row)
        const col = reservedSeat.number

        if (row !== undefined && col !== undefined) {
            initSeats[row][col - 1].status =
                reservedSeat.status === "RELEASED" ? "available" : "reserved"
        }
    })

    return initSeats
}

export default function SeatMap({
                                    session,
                                    performance,
                                    entryToken,
                                }: {
    session: PerformanceSessionResponse
    performance?: PerformanceDetailResponse
    entryToken: string
}) {
    const {areas} = useAreas({sessionId: session.id, entryToken})
    const {reserveSeats} = useCreateReservation()
    const navigate = useNavigate()

    const [selectedSection, setSelectedSection] = useState<
        keyof typeof SECTIONS | null
    >(null)
    const [detailOpen, setDetailOpen] = useState(false)
    const [selectedSeats, setSelectedSeats] = useState<
        {
            row: number
            col: number
            areaId?: number
            section: keyof typeof SECTIONS | null
            sectionName: string
            rowLabel: string
            price: number
        }[]
    >([])
    const [sectionSeats, setSectionSeats] = useState<Seats>([])

    const [zoomLevel, setZoomLevel] = useState(1)
    const [isDragging, setIsDragging] = useState(false)
    const [dragStart, setDragStart] = useState({x: 0, y: 0})
    const [scrollPosition, setScrollPosition] = useState({x: 0, y: 0})

    const mapContainerRef = useRef<HTMLDivElement>(null)

    // 영역 선택 처리
    const handleSectionSelect = (section: keyof typeof SECTIONS) => {
        if (!areas) {
            toast({
                title: "영역 정보를 찾을 수 없습니다",
                variant: "destructive",
            })
            return
        }

        if (areas.length !== 5) {
            toast({
                title: "영역 정보가 올바르지 않습니다",
                variant: "destructive",
            })
            return
        }

        const [pArea, sLArea, rArea, sRArea, aArea] = areas

        const area = (() => {
            switch (section) {
                case "P":
                    return pArea
                case "S_LEFT":
                    return sLArea
                case "R":
                    return rArea
                case "S_RIGHT":
                    return sRArea
                case "A":
                    return aArea
            }
        })()

        setSelectedSection(section)
        setSectionSeats(initializeSeats(area))

        setZoomLevel(1) // 다른 석은 기본 줌 레벨

        // setSelectedSeats([])
        setDetailOpen(true)
    }

    // 다이얼로그 내부에서 선택된 좌석을 임시로 저장할 상태 추가
    const [tempSelectedSeats, setTempSelectedSeats] = useState<
        {
            row: number
            col: number
            areaId?: number
            section: keyof typeof SECTIONS | null
            sectionName: string
            rowLabel: string
            price: number
        }[]
    >([])

    // 좌석 선택 처리 (다이얼로그 내부)
    const handleSeatToggle = (seat: Seat) => {
        if (!selectedSection) return

        const sectionName = SECTIONS[selectedSection].name
        const price = SECTIONS[selectedSection].price
        const rowLabel = getRowLabel(seat.row)

        // tempSelectedSeats에서 좌석 찾기
        const isTempSelected = tempSelectedSeats.some(
            (s) =>
                s.row === seat.row &&
                s.col === seat.col &&
                s.section === selectedSection
        )

        // selectedSeats에서 좌석 찾기
        const isAlreadySelected = selectedSeats.some(
            (s) =>
                s.row === seat.row &&
                s.col === seat.col &&
                s.section === selectedSection
        )

        if (isTempSelected) {
            // 임시 선택된 좌석이면 → 선택 해제
            setTempSelectedSeats((prev) =>
                prev.filter(
                    (s) =>
                        !(s.row === seat.row && s.col === seat.col && s.section === selectedSection)
                )
            )
        } else if (isAlreadySelected) {
            // 이미 선택된 좌석이면 → temp에 "해제 요청"으로 넣는다
            setTempSelectedSeats((prev) => [
                ...prev,
                {
                    row: seat.row,
                    col: seat.col,
                    areaId: seat.areaId,
                    section: selectedSection,
                    sectionName,
                    rowLabel,
                    price: -price, // 반영 시 제거 용도로 음수 부여
                },
            ])
        } else {
            // 새로 선택
            setTempSelectedSeats((prev) => [
                ...prev,
                {
                    row: seat.row,
                    col: seat.col,
                    areaId: seat.areaId,
                    section: selectedSection,
                    sectionName,
                    rowLabel,
                    price,
                },
            ])
        }
    }

    // 다이얼로그 확인 버튼 클릭 시 처리
    const handleDialogConfirm = () => {
        const updated = [...selectedSeats]

        tempSelectedSeats.forEach((seat) => {
            const existingIndex = updated.findIndex(
                (s) => s.row === seat.row && s.col === seat.col && s.section === seat.section
            )

            if (seat.price > 0 && existingIndex === -1) {
                // 새 좌석 추가
                updated.push(seat)
            } else if (seat.price < 0 && existingIndex !== -1) {
                // 기존 좌석 제거
                updated.splice(existingIndex, 1)
            }
        })

        setSelectedSeats(updated)
        setTempSelectedSeats([]) // 임시 상태 초기화
        setDetailOpen(false) // 다이얼로그 닫기
    }

    // 다이얼로그 취소 버튼 클릭 시 처리
    const handleDialogCancel = () => {
        setTempSelectedSeats([]) // 임시 상태 초기화
        setDetailOpen(false) // 다이얼로그 닫기
    }

    // 예약 처리
    const handleReservation = async () => {
        if (!session.id) {
            alert("공연 세션 id가 없습니다")
            return
        }

        if (selectedSeats.length === 0) {
            toast({
                title: "좌석을 선택해주세요",
                variant: "destructive",
            })
            return
        }

        let reservationId = 0

        try {
            const reservation = await reserveSeats({
                seats: selectedSeats
                    .filter((seat) => seat.areaId !== undefined)
                    .map((seat) => ({
                        areaId: seat.areaId,
                        row: seat.row + 1,
                        column: seat.col + 1,
                    })),
                sessionId: session.id,
                entryToken,
            })

            reservationId = reservation?.id ?? 0

            // 결제 페이지로 이동하면서 선택된 좌석과 세션 정보 전달
            navigate("/show/payment", {
                state: {
                    selectedSeats,
                    session,
                    performance,
                    reservationId,
                    entryToken,
                },
            })

            setDetailOpen(false)
            setSelectedSeats([])
        } catch (error) {
            console.error(error)
            toast({
                title: "예약 생성 실패",
                variant: "destructive",
            })
        }
    }

    // 확대/축소 처리
    const handleZoomIn = () => {
        setZoomLevel((prev) => Math.min(prev + 0.2, 2))
    }

    const handleZoomOut = () => {
        setZoomLevel((prev) => Math.max(prev - 0.2, 0.5))
    }

    const handleZoomChange = (value: number[]) => {
        setZoomLevel(value[0])
    }

    const handleMouseDown = (e: React.MouseEvent) => {
        if (zoomLevel > 1) {
            setIsDragging(true)
            setDragStart({x: e.clientX, y: e.clientY})
        }
    }

    const handleMouseMove = (e: React.MouseEvent) => {
        if (isDragging && zoomLevel > 1) {
            const container = e.currentTarget.parentElement
            if (container) {
                const dx = e.clientX - dragStart.x
                const dy = e.clientY - dragStart.y

                container.scrollLeft = scrollPosition.x - dx
                container.scrollTop = scrollPosition.y - dy
            }
        }
    }

    const handleMouseUp = (e: React.MouseEvent) => {
        if (isDragging) {
            setIsDragging(false)
            const container = e.currentTarget.parentElement
            if (container) {
                setScrollPosition({
                    x: container.scrollLeft,
                    y: container.scrollTop,
                })
            }
        }
    }

    const handleScroll = (e: React.UIEvent<HTMLDivElement>) => {
        setScrollPosition({
            x: e.currentTarget.scrollLeft,
            y: e.currentTarget.scrollTop,
        })
    }

    // 알파벳 행 표시 (A, B, C, ...)
    const getRowLabel = (index: number) => String.fromCharCode(65 + index)

    // 섹션 색상 가져오기
    const getSectionColor = (section: keyof typeof SECTIONS) => {
        return SECTIONS[section].color
    }

    // 섹션 테두리 색상 가져오기
    const getSectionBorderColor = (section: keyof typeof SECTIONS) => {
        switch (section) {
            case "P":
                return "#F59E0B"
            case "R":
                return "#3B82F6"
            case "S_LEFT":
            case "S_RIGHT":
                return "#10B981"
            case "A":
                return "#EF4444"
            default:
                return "#000000"
        }
    }

    return (
        <div className="flex flex-col md:flex-row gap-6 w-full">
            {/* 좌석 맵 영역 */}
            <div className="flex-1 flex flex-col items-center">
                <div
                    className="relative w-full max-w-4xl bg-white rounded-xl shadow-lg p-6"
                    ref={mapContainerRef}
                >
                    {/* 극장 내부 */}
                    <div className="w-full max-w-3xl mx-auto">
                        {/* 스테이지 */}
                        <div className="relative mb-12">
                            {/* 무대 */}
                            <div
                                className="bg-gray-800 text-white text-center py-6 rounded-lg mb-8 shadow-md relative overflow-hidden">
                                <div
                                    className="absolute inset-0 bg-[radial-gradient(circle_at_center,rgba(255,255,255,0.1),transparent_70%)]"></div>
                                <h2 className="text-xl font-bold tracking-wider">STAGE</h2>
                            </div>
                        </div>

                        {/* 좌석 배치도 - 극장 형태 */}
                        <div className="relative w-full h-[500px]">
                            {/* 좌석 영역 컨테이너 */}
                            <div className="relative w-full h-full">
                                {/* 좌석 영역 배경 - 사다리꼴 형태 */}
                                <svg
                                    viewBox="0 0 1000 600"
                                    className="absolute inset-0 w-full h-full"
                                >
                                    {/* P석 영역 - 사다리꼴 */}
                                    <path
                                        d="M350,50 L650,50 L700,150 L300,150 Z"
                                        fill={getSectionColor("P")}
                                        stroke={getSectionBorderColor("P")}
                                        strokeWidth="2"
                                        onClick={() => handleSectionSelect("P")}
                                        className="cursor-pointer hover:opacity-90 transition-opacity"
                                    />

                                    {/* R석 영역 - 사다리꼴 (크기 축소) */}
                                    <path
                                        d="M400,150 L600,150 L630,250 L370,250 Z"
                                        fill={getSectionColor("R")}
                                        stroke={getSectionBorderColor("R")}
                                        strokeWidth="2"
                                        onClick={() => handleSectionSelect("R")}
                                        className="cursor-pointer hover:opacity-90 transition-opacity"
                                    />

                                    {/* S석 영역 (좌) - 사다리꼴 (크기 축소) */}
                                    <path
                                        d="M250,150 L400,150 L370,250 L200,250 Z"
                                        fill={getSectionColor("S_LEFT")}
                                        stroke={getSectionBorderColor("S_LEFT")}
                                        strokeWidth="2"
                                        onClick={() => handleSectionSelect("S_LEFT")}
                                        className="cursor-pointer hover:opacity-90 transition-opacity"
                                    />

                                    {/* S석 영역 (우) - 사다리꼴 (크기 축소) */}
                                    <path
                                        d="M600,150 L750,150 L800,250 L630,250 Z"
                                        fill={getSectionColor("S_RIGHT")}
                                        stroke={getSectionBorderColor("S_RIGHT")}
                                        strokeWidth="2"
                                        onClick={() => handleSectionSelect("S_RIGHT")}
                                        className="cursor-pointer hover:opacity-90 transition-opacity"
                                    />

                                    {/* A석 영역 - 사다리꼴 */}
                                    <path
                                        d="M100,250 L900,250 L950,450 L50,450 Z"
                                        fill={getSectionColor("A")}
                                        stroke={getSectionBorderColor("A")}
                                        strokeWidth="2"
                                        onClick={() => handleSectionSelect("A")}
                                        className="cursor-pointer hover:opacity-90 transition-opacity"
                                    />

                                    {/* 통로 */}
                                    <path
                                        d="M480,50 L520,50 L520,450 L480,450 Z"
                                        fill="#F3F4F6"
                                        stroke="#E5E7EB"
                                        strokeWidth="1"
                                    />
                                    <text
                                        x="500"
                                        y="470"
                                        textAnchor="middle"
                                        fill="#6B7280"
                                        fontSize="12"
                                    >
                                        중앙 통로
                                    </text>

                                    <path
                                        d="M280,250 L320,250 L320,450 L280,450 Z"
                                        fill="#F3F4F6"
                                        stroke="#E5E7EB"
                                        strokeWidth="1"
                                    />

                                    <path
                                        d="M680,250 L720,250 L720,450 L680,450 Z"
                                        fill="#F3F4F6"
                                        stroke="#E5E7EB"
                                        strokeWidth="1"
                                    />

                                    {/* 좌석 등급 표시 */}
                                    <text
                                        x="500"
                                        y="100"
                                        textAnchor="middle"
                                        fill="#92400E"
                                        fontWeight="bold"
                                        fontSize="16"
                                    >
                                        P석
                                    </text>
                                    <text
                                        x="500"
                                        y="200"
                                        textAnchor="middle"
                                        fill="#1E40AF"
                                        fontWeight="bold"
                                        fontSize="16"
                                    >
                                        R석
                                    </text>
                                    <text
                                        x="300"
                                        y="200"
                                        textAnchor="middle"
                                        fill="#065F46"
                                        fontWeight="bold"
                                        fontSize="16"
                                    >
                                        S석
                                    </text>
                                    {/*S석 좌측*/}
                                    <text
                                        x="700"
                                        y="200"
                                        textAnchor="middle"
                                        fill="#065F46"
                                        fontWeight="bold"
                                        fontSize="16"
                                    >
                                        S석
                                    </text>
                                    {/*S석 우측*/}
                                    <text
                                        x="500"
                                        y="350"
                                        textAnchor="middle"
                                        fill="#991B1B"
                                        fontWeight="bold"
                                        fontSize="16"
                                    >
                                        A석
                                    </text>

                                    {/* 입구 표시 */}
                                    <rect
                                        x="50"
                                        y="450"
                                        width="60"
                                        height="30"
                                        rx="5"
                                        fill="#F3F4F6"
                                        stroke="#E5E7EB"
                                    />
                                    <text
                                        x="80"
                                        y="470"
                                        textAnchor="middle"
                                        fill="#6B7280"
                                        fontSize="12"
                                    >
                                        입구
                                    </text>

                                    <rect
                                        x="890"
                                        y="450"
                                        width="60"
                                        height="30"
                                        rx="5"
                                        fill="#F3F4F6"
                                        stroke="#E5E7EB"
                                    />
                                    <text
                                        x="920"
                                        y="470"
                                        textAnchor="middle"
                                        fill="#6B7280"
                                        fontSize="12"
                                    >
                                        입구
                                    </text>
                                </svg>

                                {/* 좌석 미니어처 표시 - 더 명확하게 */}
                                <svg
                                    viewBox="0 0 1000 600"
                                    className="absolute inset-0 w-full h-full pointer-events-none"
                                >
                                    {/* P석 좌석 패턴 */}
                                    <pattern
                                        id="seat-pattern-p"
                                        width="10"
                                        height="10"
                                        patternUnits="userSpaceOnUse"
                                    >
                                        <rect width="4" height="4" fill="#92400E" rx="1"/>
                                    </pattern>
                                    <path
                                        d="M370,70 L630,70 L670,130 L330,130 Z"
                                        fill="url(#seat-pattern-p)"
                                        fillOpacity="0.3"
                                    />

                                    {/* R석 좌석 패턴 */}
                                    <pattern
                                        id="seat-pattern-r"
                                        width="10"
                                        height="10"
                                        patternUnits="userSpaceOnUse"
                                    >
                                        <rect width="4" height="4" fill="#1E40AF" rx="1"/>
                                    </pattern>
                                    <path
                                        d="M420,170 L580,170 L600,230 L400,230 Z"
                                        fill="url(#seat-pattern-r)"
                                        fillOpacity="0.3"
                                    />

                                    {/* S석 (좌) 좌석 패턴 */}
                                    <pattern
                                        id="seat-pattern-s-left"
                                        width="10"
                                        height="10"
                                        patternUnits="userSpaceOnUse"
                                    >
                                        <rect width="4" height="4" fill="#065F46" rx="1"/>
                                    </pattern>
                                    <path
                                        d="M270,170 L380,170 L350,230 L220,230 Z"
                                        fill="url(#seat-pattern-s-left)"
                                        fillOpacity="0.3"
                                    />

                                    {/* S석 (우) 좌석 패턴 */}
                                    <pattern
                                        id="seat-pattern-s-right"
                                        width="10"
                                        height="10"
                                        patternUnits="userSpaceOnUse"
                                    >
                                        <rect width="4" height="4" fill="#065F46" rx="1"/>
                                    </pattern>
                                    <path
                                        d="M620,170 L730,170 L780,230 L650,230 Z"
                                        fill="url(#seat-pattern-s-right)"
                                        fillOpacity="0.3"
                                    />

                                    {/* A석 좌석 패턴 */}
                                    <pattern
                                        id="seat-pattern-a"
                                        width="10"
                                        height="10"
                                        patternUnits="userSpaceOnUse"
                                    >
                                        <rect width="4" height="4" fill="#991B1B" rx="1"/>
                                    </pattern>
                                    <path
                                        d="M120,270 L880,270 L920,430 L80,430 Z"
                                        fill="url(#seat-pattern-a)"
                                        fillOpacity="0.3"
                                    />
                                </svg>
                            </div>
                        </div>

                        {/* 좌석 안내 */}
                        <div className="mt-8 grid grid-cols-2 md:grid-cols-4 gap-4 max-w-3xl mx-auto">
                            <div className="flex items-center bg-white p-3 rounded-lg shadow-sm border">
                                <div className="w-4 h-4 bg-amber-100 border border-amber-500 mr-2 rounded"></div>
                                <span className="text-sm font-medium">P석 - 150,000원</span>
                            </div>
                            <div className="flex items-center bg-white p-3 rounded-lg shadow-sm border">
                                <div className="w-4 h-4 bg-blue-100 border border-blue-500 mr-2 rounded"></div>
                                <span className="text-sm font-medium">R석 - 120,000원</span>
                            </div>
                            <div className="flex items-center bg-white p-3 rounded-lg shadow-sm border">
                                <div className="w-4 h-4 bg-emerald-100 border border-emerald-500 mr-2 rounded"></div>
                                <span className="text-sm font-medium">S석 - 100,000원</span>
                            </div>
                            <div className="flex items-center bg-white p-3 rounded-lg shadow-sm border">
                                <div className="w-4 h-4 bg-red-100 border border-red-500 mr-2 rounded"></div>
                                <span className="text-sm font-medium">A석 - 80,000원</span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            {/* 우측 패널 - 선택한 좌석 정보 */}
            <div className="w-full md:w-80 lg:w-96 border rounded-lg p-4 h-fit sticky top-4 bg-white shadow-md">
                <div className="flex items-center gap-2 mb-4 pb-2 border-b">
                    <Ticket className="w-5 h-5 text-primary"/>
                    <h2 className="text-xl font-bold">선택한 좌석</h2>
                </div>

                {selectedSeats.length === 0 ? (
                    <div className="text-center py-12 text-gray-500">
                        <div
                            className="w-16 h-16 mx-auto mb-4 rounded-full bg-gray-100 flex items-center justify-center">
                            <Ticket className="w-8 h-8 text-gray-400"/>
                        </div>
                        <p>선택한 좌석이 없습니다.</p>
                        <p className="text-sm mt-2">
                            좌석 영역을 클릭하여 좌석을 선택해주세요.
                        </p>
                    </div>
                ) : (
                    <>
                        <div className="max-h-[400px] overflow-y-auto mb-4 pr-1">
                            {selectedSeats.map((seat, index) => (
                                <div
                                    key={index}
                                    className="flex justify-between items-center py-3 border-b last:border-b-0 hover:bg-gray-50 rounded-md px-2 group"
                                >
                                    <div>
                                        <span className="font-medium">{seat.sectionName}</span>
                                        <span className="text-sm text-gray-600 ml-2">
                      {seat.rowLabel}행 {seat.col + 1}번
                    </span>
                                    </div>
                                    <div className="flex items-center">
                    <span className="mr-2 text-sm font-medium">
                      {seat.price.toLocaleString()}원
                    </span>
                                        <button
                                            className="text-red-400 hover:text-red-600 p-1 rounded-full hover:bg-red-50 opacity-0 group-hover:opacity-100 transition-opacity"
                                            onClick={() => {
                                                setSelectedSeats(
                                                    selectedSeats.filter((_, i) => i !== index),
                                                )
                                            }}
                                        >
                                            ×
                                        </button>
                                    </div>
                                </div>
                            ))}
                        </div>

                        <div className="border-t pt-4 bg-gray-50 -mx-4 -mb-4 p-4 rounded-b-lg">
                            <div className="flex justify-between items-center mb-2">
                                <span className="text-gray-600">선택 좌석 수</span>
                                <span className="font-medium">{selectedSeats.length}석</span>
                            </div>
                            <div className="flex justify-between items-center mb-4">
                                <span className="text-gray-600">총 가격</span>
                                <span className="font-bold text-lg text-primary">
                  {selectedSeats
                      .reduce((sum, seat) => sum + seat.price, 0)
                      .toLocaleString()}
                                    원
                </span>
                            </div>
                            <Button className="w-full" onClick={handleReservation}>
                                예약하기
                            </Button>
                        </div>
                    </>
                )}
            </div>

            {/* 좌석 선택 다이얼로그 */}
            <Dialog
                open={detailOpen}
                onOpenChange={(isOpen) => {
                    if (!isOpen) {
                        setTempSelectedSeats([]) // 다이얼로그 닫힐 때 temp 초기화
                    }
                    setDetailOpen(isOpen)
                }}
            >
                <DialogContent className="max-[100vw]">
                    <DialogHeader>
                        <DialogTitle className="flex items-center gap-2">
                            {selectedSection && (
                                <>
                                    <div
                                        className={cn(
                                            "w-3 h-3 rounded-full",
                                            selectedSection === "P" && "bg-amber-500",
                                            selectedSection === "R" && "bg-blue-500",
                                            (selectedSection === "S_LEFT" ||
                                                selectedSection === "S_RIGHT") &&
                                            "bg-emerald-500",
                                            selectedSection === "A" && "bg-red-500",
                                        )}
                                    ></div>
                                    {SECTIONS[selectedSection].name} 좌석 선택
                                </>
                            )}
                        </DialogTitle>
                    </DialogHeader>

                    {selectedSection && (
                        <div className="flex flex-col items-center">
                            <div className="bg-gray-100 text-center py-2 px-4 rounded-lg mb-4 w-full">
                                <span className="text-gray-700 font-medium">스테이지 방향</span>
                            </div>

                            <div className="flex items-center justify-between w-full mb-4">
                                <div className="flex items-center gap-2">
                                    <Button
                                        variant="outline"
                                        size="icon"
                                        onClick={handleZoomOut}
                                        disabled={zoomLevel <= 0.5}
                                        className="border-gray-200 hover:bg-gray-100"
                                    >
                                        <Minus className="h-4 w-4"/>
                                    </Button>
                                    <div className="w-[100px]">
                                        <Slider
                                            value={[zoomLevel]}
                                            min={0.5}
                                            max={2}
                                            step={0.1}
                                            onValueChange={handleZoomChange}
                                        />
                                    </div>
                                    <Button
                                        variant="outline"
                                        size="icon"
                                        onClick={handleZoomIn}
                                        disabled={zoomLevel >= 2}
                                        className="border-gray-200 hover:bg-gray-100"
                                    >
                                        <Plus className="h-4 w-4"/>
                                    </Button>
                                    <span className="text-xs text-gray-500 ml-2">
                    {Math.round(zoomLevel * 100)}%
                  </span>
                                </div>
                                <div className="text-xs text-gray-500">
                                    <ZoomIn className="h-4 w-4 inline mr-1"/>
                                    드래그하여 이동
                                </div>
                            </div>

                            <div
                                className="overflow-auto max-w-full max-h-[65vh] p-4 border rounded-lg bg-white shadow-inner"
                                onScroll={handleScroll}
                            >
                                <div
                                    className="grid gap-1 mb-4"
                                    style={{
                                        transform: `scale(${zoomLevel})`,
                                        transformOrigin: "top left",
                                        transition: "transform 0.2s ease",
                                    }}
                                    onMouseDown={handleMouseDown}
                                    onMouseMove={handleMouseMove}
                                    onMouseUp={handleMouseUp}
                                    onMouseLeave={() => setIsDragging(false)}
                                >
                                    {/* 스테이지 표시 */}
                                    <div
                                        className="w-full text-center mb-2 text-xs text-gray-500 bg-gray-100 py-1 rounded">
                                        STAGE
                                    </div>

                                    {/* 좌석 배치 - 직사각형 형태로 표현 */}
                                    <div className="flex flex-col items-center">
                                        {sectionSeats.map((seatRow, rowIndex) => (
                                            <div
                                                key={rowIndex}
                                                className="flex items-center mb-1"
                                                style={{
                                                    // A석은 직사각형 형태로 유지
                                                    transform: "none",
                                                }}
                                            >
                                                <div className="w-6 text-center mr-2 font-medium text-gray-500">
                                                    {getRowLabel(rowIndex)}
                                                </div>
                                                <div className="flex gap-1">
                                                    {seatRow.map((seat, colIndex) => {
                                                        const isReserved = seat.status !== "available"
                                                        
                                                        const isSelected = (() => {
                                                            const key = (s: any) => `${s.row}-${s.col}-${s.section}`
                                                            const selectedMap = new Map(
                                                                selectedSeats.map((s) => [key(s), true])
                                                            )

                                                            tempSelectedSeats.forEach((s) => {
                                                                const k = key(s)
                                                                if (s.price < 0) {
                                                                    selectedMap.delete(k)
                                                                } else {
                                                                    selectedMap.set(k, true)
                                                                }
                                                            })

                                                            return selectedMap.has(`${rowIndex}-${colIndex}-${selectedSection}`)
                                                        })()


                                                        // 좌석 색상 설정
                                                        let seatColor = ""
                                                        let textColor = ""

                                                        if (isSelected) {
                                                            switch (selectedSection) {
                                                                case "P":
                                                                    seatColor = "bg-amber-200 border-amber-500"
                                                                    textColor = "text-amber-900"
                                                                    break
                                                                case "R":
                                                                    seatColor = "bg-blue-200 border-blue-500"
                                                                    textColor = "text-blue-900"
                                                                    break
                                                                case "S_LEFT":
                                                                case "S_RIGHT":
                                                                    seatColor =
                                                                        "bg-emerald-200 border-emerald-500"
                                                                    textColor = "text-emerald-900"
                                                                    break
                                                                case "A":
                                                                    seatColor = "bg-red-200 border-red-500"
                                                                    textColor = "text-red-900"
                                                                    break
                                                            }
                                                        }

                                                        return (
                                                            <button
                                                                key={colIndex}
                                                                className={cn(
                                                                    "w-6 h-6 text-xs border rounded-md flex items-center justify-center transition-all duration-200",
                                                                    isReserved
                                                                        ? "bg-gray-300 border-gray-400 cursor-not-allowed"
                                                                        : isSelected
                                                                            ? `${seatColor} ${textColor} shadow-sm`
                                                                            : "bg-white hover:bg-gray-50 border-gray-200",
                                                                )}
                                                                onClick={() => {
                                                                    if (isReserved) {
                                                                        toast({
                                                                            title: "이미 예약된 좌석입니다",
                                                                            variant: "destructive",
                                                                        })
                                                                    } else {
                                                                        handleSeatToggle(seat)
                                                                    }
                                                                }}
                                                            >
                                                                {colIndex + 1}
                                                            </button>
                                                        )
                                                    })}
                                                </div>
                                            </div>
                                        ))}
                                    </div>
                                </div>
                            </div>

                            <div className="flex items-center justify-between w-full mt-4">
                                <div>
                                    <p className="text-sm text-gray-500">
                                        선택된 좌석: {
                                        (() => {
                                            const key = (s: any) => `${s.row}-${s.col}-${s.section}`
                                            const seatMap = new Map<string, typeof selectedSeats[0]>()

                                            selectedSeats.forEach((s) => {
                                                if (s.section === selectedSection) {
                                                    seatMap.set(key(s), s)
                                                }
                                            })

                                            tempSelectedSeats.forEach((s) => {
                                                if (s.section !== selectedSection) return
                                                const k = key(s)
                                                if (s.price < 0) {
                                                    seatMap.delete(k)
                                                } else {
                                                    seatMap.set(k, s)
                                                }
                                            })

                                            return seatMap.size
                                        })()
                                    }석
                                    </p>
                                    {[...tempSelectedSeats, ...selectedSeats].filter(
                                        (seat) => seat.section === selectedSection,
                                    ).length > 0 && (
                                        <p className="font-medium">
                                            섹션 가격:{
                                            (() => {
                                                const key = (s: any) => `${s.row}-${s.col}-${s.section}`
                                                const seatMap = new Map<string, typeof selectedSeats[0]>()

                                                selectedSeats.forEach((s) => {
                                                    if (s.section === selectedSection) {
                                                        seatMap.set(key(s), s)
                                                    }
                                                })

                                                tempSelectedSeats.forEach((s) => {
                                                    if (s.section !== selectedSection) return
                                                    const k = key(s)
                                                    if (s.price < 0) {
                                                        seatMap.delete(k)
                                                    } else {
                                                        seatMap.set(k, s)
                                                    }
                                                })

                                                const total = Array.from(seatMap.values()).reduce((acc, s) => acc + s.price, 0)
                                                return `${total.toLocaleString()}원`
                                            })()
                                        }
                                        </p>
                                    )}
                                </div>
                                <div className="flex gap-2">
                                    <Button
                                        variant="outline"
                                        onClick={handleDialogCancel}
                                        className="border-gray-200"
                                    >
                                        취소
                                    </Button>
                                    <Button onClick={handleDialogConfirm}>확인</Button>
                                </div>
                            </div>
                        </div>
                    )}
                </DialogContent>
            </Dialog>
        </div>
    )
}

const convertRowToNumber = (row?: string) => {
    if (!row) return

    return row.toUpperCase().charCodeAt(0) - 65 // 'A'의 ASCII 코드는 65
}
