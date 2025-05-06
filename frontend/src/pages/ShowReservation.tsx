import { PageLayout } from "../layout/PageLayout"
import SeatMap from "@/components/reservation/SeatMap"

export const ShowReservation = () => {
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
