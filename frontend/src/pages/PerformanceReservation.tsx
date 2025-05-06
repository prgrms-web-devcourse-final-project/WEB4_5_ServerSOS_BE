import { useState } from "react"
import { PageLayout } from "../layout/PageLayout"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { MinusIcon, PlusIcon } from "lucide-react"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import SeatMap from "@/components/reservation/SeatMap"
import SelectedSeatsPanel from "@/components/reservation/SelectedSeatPanel"
import SeatLegend from "@/components/reservation/SeatLegend"

export const PerformanceReservation = () => {
  const [selectedSection, setSelectedSection] = useState<string | null>(null)
  const [selectedSeats, setSelectedSeats] = useState<string[]>([])
  const [scale, setScale] = useState(1)

  const handleSectionSelect = (section: string) => {
    setSelectedSection(section)
  }

  const handleSeatSelect = (seatId: string) => {
    if (selectedSeats.includes(seatId)) {
      setSelectedSeats(selectedSeats.filter((id) => id !== seatId))
    } else {
      setSelectedSeats([...selectedSeats, seatId])
    }
  }

  const handleZoomIn = () => {
    setScale((prev) => Math.min(prev + 0.2, 2))
  }

  const handleZoomOut = () => {
    setScale((prev) => Math.max(prev - 0.2, 0.6))
  }

  const handleResetZoom = () => {
    setScale(1)
  }

  return (
    <PageLayout>
      <div className="container mx-auto py-8 px-4">
        <h1 className="text-3xl font-bold text-center mb-8">좌석 예매</h1>

        <div className="max-w-7xl mx-auto">
          <SeatMap />
        </div>
      </div>
    </PageLayout>
  )
}
