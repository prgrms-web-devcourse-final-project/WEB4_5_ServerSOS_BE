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
      (
      <div className="container mx-auto py-8 px-4">
        <h1 className="text-3xl font-bold text-center mb-8">좌석 예매</h1>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <div className="lg:col-span-2">
            <Card>
              <CardHeader className="pb-2">
                <CardTitle className="text-xl flex justify-between items-center">
                  <span>좌석 배치도</span>
                  <div className="flex items-center space-x-2">
                    <Button
                      variant="outline"
                      size="icon"
                      onClick={handleZoomOut}
                      aria-label="축소"
                    >
                      <MinusIcon className="h-4 w-4" />
                    </Button>
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={handleResetZoom}
                    >
                      100%
                    </Button>
                    <Button
                      variant="outline"
                      size="icon"
                      onClick={handleZoomIn}
                      aria-label="확대"
                    >
                      <PlusIcon className="h-4 w-4" />
                    </Button>
                  </div>
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="relative overflow-auto border rounded-lg p-4 h-[500px] flex items-center justify-center">
                  <SeatMap
                    selectedSection={selectedSection}
                    selectedSeats={selectedSeats}
                    onSectionSelect={handleSectionSelect}
                    onSeatSelect={handleSeatSelect}
                    scale={scale}
                  />
                </div>
              </CardContent>
            </Card>
          </div>

          <div>
            <Card className="mb-6">
              <CardHeader>
                <CardTitle>섹션 선택</CardTitle>
              </CardHeader>
              <CardContent>
                <Tabs
                  defaultValue={selectedSection || "all"}
                  onValueChange={handleSectionSelect}
                >
                  <TabsList className="grid grid-cols-5 mb-4">
                    <TabsTrigger value="all">전체</TabsTrigger>
                    <TabsTrigger value="R">R석</TabsTrigger>
                    <TabsTrigger value="S">S석</TabsTrigger>
                    <TabsTrigger value="A">A석</TabsTrigger>
                    <TabsTrigger value="B">B석</TabsTrigger>
                  </TabsList>

                  <TabsContent value="all">
                    <p className="text-sm text-muted-foreground">
                      전체 좌석을 확인하실 수 있습니다. 섹션을 선택하시면 해당
                      섹션의 좌석을 선택하실 수 있습니다.
                    </p>
                  </TabsContent>
                  <TabsContent value="R">
                    <p className="text-sm text-muted-foreground">
                      R석은 최고급 좌석으로, 무대와 가장 가까운 위치에 있습니다.
                    </p>
                  </TabsContent>
                  <TabsContent value="S">
                    <p className="text-sm text-muted-foreground">
                      S석은 프리미엄 좌석으로, 무대를 정면에서 볼 수 있는 좋은
                      위치에 있습니다.
                    </p>
                  </TabsContent>
                  <TabsContent value="A">
                    <p className="text-sm text-muted-foreground">
                      A석은 스탠다드 좌석으로, 무대를 잘 볼 수 있는 위치에
                      있습니다.
                    </p>
                  </TabsContent>
                  <TabsContent value="B">
                    <p className="text-sm text-muted-foreground">
                      B석은 이코노미 좌석으로, 합리적인 가격으로 공연을 즐길 수
                      있습니다.
                    </p>
                  </TabsContent>
                </Tabs>
              </CardContent>
            </Card>

            <SelectedSeatsPanel
              selectedSeats={selectedSeats}
              onRemoveSeat={handleSeatSelect}
            />

            <SeatLegend />

            <div className="mt-6">
              <Button
                className="w-full"
                size="lg"
                disabled={selectedSeats.length === 0}
              >
                예매 계속하기
              </Button>
            </div>
          </div>
        </div>
      </div>
      )
    </PageLayout>
  )
}
