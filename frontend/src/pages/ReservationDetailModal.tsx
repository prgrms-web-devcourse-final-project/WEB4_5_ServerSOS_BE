import { useEffect, useState } from "react";
import { apiClient } from "@/api/apiClient";

export function ReservationDetailModal({
  id,
  onClose,
}: {
  id: number;
  onClose: () => void;
}) {
  const [data, setData] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);

  useEffect(() => {
    const fetch = async () => {
      try {
        const res = await apiClient.reservation.getReservation({ id });
        setData(res.data);
      } catch (e) {
        setError(true);
        console.error("예약 상세 조회 오류:", e);
      } finally {
        setLoading(false);
      }
    };
    fetch();
  }, [id]);

  return (
    <div className="fixed inset-0 bg-black bg-opacity-40 flex justify-center items-center z-50">
      <div className="bg-white p-6 rounded-lg w-full max-w-md shadow-md relative">
        <button
          onClick={onClose}
          className="absolute top-2 right-2 text-gray-500 hover:text-black"
        >
          ✕
        </button>

        <h2 className="text-xl font-bold mb-4">예약 상세 정보</h2>

        {loading && <p>불러오는 중...</p>}
        {error && (
          <p className="text-red-500">정보를 불러오는 데 실패했습니다.</p>
        )}
        {data && (
          <div className="space-y-2">
            <p>
              <strong>예약 ID:</strong> {data.id}
            </p>
            <p>
              <strong>예약일:</strong>{" "}
              {new Date(data.reservationTime).toLocaleString()}
            </p>
            <p>
              <strong>상태:</strong> {data.status}
            </p>
            <p>
              <strong>공연명:</strong> {data.performance.name}
            </p>
            <p>
              <strong>공연장명:</strong> {data.venue.name}
            </p>
            <p>
              <strong>주소:</strong> {data.venue.address}
            </p>
            <p>
              <strong>총 가격:</strong> {data.totalPrice?.toLocaleString()}원
            </p>
            <div>
              <p className="text-sm text-gray-600">좌석 정보</p>
              <div className="space-y-1">
                {data.seats.map((seat, seatIndex) => (
                  <p key={seatIndex} className="font-medium text-sm">
                    {seat.areaName} {seat.row}열 {seat.number}번
                  </p>
                ))}
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
