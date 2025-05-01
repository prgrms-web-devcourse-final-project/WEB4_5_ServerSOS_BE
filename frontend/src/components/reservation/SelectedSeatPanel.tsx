import { X } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"

interface SelectedSeatsPanelProps {
  selectedSeats: string[]
  onRemoveSeat: (seatId: string) => void
}

export default function SelectedSeatsPanel({
  selectedSeats,
  onRemoveSeat,
}: SelectedSeatsPanelProps) {
  // 좌석 가격 계산
  const calculatePrice = (seatId: string) => {
    const section = seatId.split("-")[0]

    switch (section) {
      case "R":
        return 150000
      case "S":
        return 120000
      case "A":
        return 90000
      case "B":
        return 60000
      default:
        return 0
    }
  }

  // 총 가격 계산
  const totalPrice = selectedSeats.reduce(
    (sum, seatId) => sum + calculatePrice(seatId),
    0,
  )

  return (
    <Card>
      <CardHeader>
        <CardTitle>선택한 좌석</CardTitle>
      </CardHeader>
      <CardContent>
        {selectedSeats.length === 0 ? (
          <p className="text-sm text-muted-foreground">
            선택한 좌석이 없습니다.
          </p>
        ) : (
          <div className="space-y-4">
            <ul className="space-y-2">
              {selectedSeats.map((seatId) => (
                <li
                  key={seatId}
                  className="flex justify-between items-center p-2 bg-gray-50 rounded"
                >
                  <span>{seatId}</span>
                  <div className="flex items-center">
                    <span className="mr-2">
                      {calculatePrice(seatId).toLocaleString()}원
                    </span>
                    <Button
                      variant="ghost"
                      size="icon"
                      className="h-6 w-6"
                      onClick={() => onRemoveSeat(seatId)}
                    >
                      <X className="h-4 w-4" />
                      <span className="sr-only">좌석 선택 취소</span>
                    </Button>
                  </div>
                </li>
              ))}
            </ul>

            <div className="flex justify-between font-medium pt-2 border-t">
              <span>총 금액</span>
              <span>{totalPrice.toLocaleString()}원</span>
            </div>
          </div>
        )}
      </CardContent>
    </Card>
  )
}
