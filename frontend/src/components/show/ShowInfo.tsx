interface ShowInfoProps {
  description: string
  detailImages: string[]
}

export default function ShowInfo({ description, detailImages }: ShowInfoProps) {
  return (
    <div className="space-y-8">
      {/* 공연 설명 */}
      <div className="prose max-w-none whitespace-pre-wrap">{description}</div>

      {/* 공연 상세 이미지 */}
      <div className="space-y-6 mt-8">
        <h3 className="text-xl font-bold">공연 상세</h3>
        <div className="space-y-6">
          {detailImages.map((image, index) => (
            <div
              key={index}
              className="relative w-full aspect-[4/3] rounded-lg shadow-md"
            >
              <img
                src={image || "/placeholder.svg"}
                alt={`공연 상세 이미지 ${index + 1}`}
                className="object-cover"
              />
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}
