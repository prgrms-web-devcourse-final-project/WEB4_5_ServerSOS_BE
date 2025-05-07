import GenreRanking from "./GenreRanking"
import UpcomingShows from "./UpcomingShows"

export const Sections = () => {
  return (
    <>
      <section className="py-16 container mx-auto px-4">
        <h2 className="text-2xl font-bold text-center mb-10">장르별 랭킹</h2>
        <GenreRanking />
      </section>

      {/* 오픈 예정 공연 */}
      <section className="py-16 bg-gray-50">
        <div className="container mx-auto px-4">
          <h2 className="text-2xl font-bold text-center mb-10">
            오픈 예정 공연
          </h2>
          <UpcomingShows />
        </div>
      </section>
    </>
  )
}
