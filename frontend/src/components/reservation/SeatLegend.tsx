export default function SeatLegend() {
  return (
    <div className="mt-6 bg-white rounded-lg border p-4">
      <h3 className="font-medium mb-3">좌석 안내</h3>
      <div className="grid grid-cols-2 gap-2 text-sm">
        <div className="flex items-center">
          <div className="w-4 h-4 rounded-full bg-[#fda4af] mr-2"></div>
          <span>R석 (150,000원)</span>
        </div>
        <div className="flex items-center">
          <div className="w-4 h-4 rounded-full bg-[#c4b5fd] mr-2"></div>
          <span>S석 (120,000원)</span>
        </div>
        <div className="flex items-center">
          <div className="w-4 h-4 rounded-full bg-[#93c5fd] mr-2"></div>
          <span>A석 (90,000원)</span>
        </div>
        <div className="flex items-center">
          <div className="w-4 h-4 rounded-full bg-[#6ee7b7] mr-2"></div>
          <span>B석 (60,000원)</span>
        </div>
        <div className="flex items-center">
          <div className="w-4 h-4 rounded-full bg-[#f43f5e] mr-2"></div>
          <span>선택한 좌석</span>
        </div>
        <div className="flex items-center">
          <div className="w-4 h-4 rounded-full bg-[#6b7280] mr-2"></div>
          <span>판매 완료</span>
        </div>
      </div>
    </div>
  )
}
