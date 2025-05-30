import { useMyReservationList } from "@/hooks/useMyReservationList";
import { useReservationCancel } from "@/hooks/useReservationCancel";
import { ReservationDetailModal } from "./ReservationDetailModal";
import { PageLayout } from "@/layout/PageLayout";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { useToast } from "@/components/ui/use-toast";
import { format } from "date-fns";
import { useCallback, useRef, useState } from "react";
import { Calendar, MapPin, Receipt, User, Trash2 } from "lucide-react";

export const MyReservationList = () => {
  const {
    myReservationList,
    isLoading,
    error,
    fetchNextPage,
    hasNextPage,
    isFetchingNextPage,
  } = useMyReservationList();

  const { cancelReservation, isPending: isCancelling } = useReservationCancel();
  const { toast } = useToast();
  const [selectedReservationId, setSelectedReservationId] = useState<
    number | null
  >(null);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [selectedReservationIdForModal, setSelectedReservationIdForModal] =
    useState<number | null>(null);

  const openDetailModal = (reservationId: number) => {
    setSelectedReservationIdForModal(reservationId);
  };

  const closeDetailModal = () => {
    setSelectedReservationIdForModal(null);
  };

  const observer = useRef<IntersectionObserver | null>(null);
  const lastReservationElementRef = useCallback(
    (node: HTMLDivElement) => {
      if (isLoading) return;
      if (observer.current) observer.current.disconnect();
      observer.current = new IntersectionObserver((entries) => {
        if (entries[0].isIntersecting && hasNextPage && !isFetchingNextPage) {
          fetchNextPage();
        }
      });
      if (node) observer.current.observe(node);
    },
    [isLoading, hasNextPage, isFetchingNextPage, fetchNextPage]
  );

  const handleDeleteClick = (reservationId: number | undefined) => {
    if (reservationId) {
      setSelectedReservationId(reservationId);
      setIsDeleteModalOpen(true);
    }
  };

  const handleDeleteConfirm = () => {
    if (selectedReservationId) {
      cancelReservation(selectedReservationId, {
        onSuccess: () => {
          toast({
            title: "예약이 취소되었습니다",
            description: "예약이 성공적으로 취소되었습니다.",
            variant: "default",
          });
          setIsDeleteModalOpen(false);
          setSelectedReservationId(null);
        },
        onError: (error) => {
          toast({
            title: "예약 취소 실패",
            description: "예약 취소 중 오류가 발생했습니다. 다시 시도해주세요.",
            variant: "destructive",
          });
          console.error("예약 취소 오류:", error);
        },
      });
    }
  };

  const handleDeleteCancel = () => {
    setIsDeleteModalOpen(false);
    setSelectedReservationId(null);
  };

  // 취소 가능한 상태인지 확인하는 함수
  const isCancellable = (status?: string) => {
    return status === "PAID" || status === "RESERVED";
  };

  if (isLoading && myReservationList.length === 0) {
    return (
      <PageLayout>
        <div className="container mx-auto py-8 px-4">
          <h1 className="text-3xl font-bold mb-8">나의 예약 내역</h1>
          <div className="flex justify-center items-center py-20">
            <div className="inline-block w-8 h-8 border-4 border-blue-400 border-t-transparent rounded-full animate-spin"></div>
            <span className="ml-3 text-gray-600">
              예약 내역을 불러오는 중...
            </span>
          </div>
        </div>
      </PageLayout>
    );
  }

  if (error) {
    return (
      <PageLayout>
        <div className="container mx-auto py-8 px-4">
          <h1 className="text-3xl font-bold mb-8">나의 예약 내역</h1>
          <div className="text-center py-10 text-red-500">
            오류가 발생했습니다: {error.message}
          </div>
        </div>
      </PageLayout>
    );
  }

  const getStatusColor = (status?: string) => {
    switch (status) {
      case "PAID":
        return "bg-green-100 text-green-800";
      case "CANCELED":
        return "bg-red-100 text-red-800";
      case "RESERVED":
        return "bg-blue-100 text-blue-800";
      case "EXPIRED":
        return "bg-gray-100 text-gray-800";
      default:
        return "bg-gray-100 text-gray-800";
    }
  };

  const getStatusText = (status?: string) => {
    switch (status) {
      case "PAID":
        return "결제완료";
      case "CANCELED":
        return "취소됨";
      case "RESERVED":
        return "예약됨";
      case "EXPIRED":
        return "만료됨";
      default:
        return "알 수 없음";
    }
  };

  return (
    <PageLayout>
      <div className="container mx-auto py-8 px-4">
        <h1 className="text-3xl font-bold mb-8">나의 예약 내역</h1>

        {myReservationList.length === 0 ? (
          <div className="text-center py-20">
            <div className="text-gray-500 text-lg mb-4">
              예약 내역이 없습니다.
            </div>
            <p className="text-gray-400">
              공연을 예약하고 여기서 확인해보세요!
            </p>
          </div>
        ) : (
          <div className="space-y-6">
            {myReservationList.map((reservation, index) => {
              const isLast = index === myReservationList.length - 1;
              return (
                <Card
                  key={`${reservation.id}-${index}`}
                  ref={isLast ? lastReservationElementRef : null}
                  className="hover:shadow-lg transition-shadow cursor-pointer"
                  onClick={() => openDetailModal(reservation.id)}
                >
                  <CardHeader>
                    <div className="flex justify-between items-start">
                      <div>
                        <CardTitle className="text-xl mb-2">
                          예약 번호: {reservation.id}
                        </CardTitle>
                        <div className="flex items-center gap-2">
                          <span
                            className={`px-3 py-1 rounded-full text-sm font-medium ${getStatusColor(
                              reservation.status
                            )}`}
                          >
                            {getStatusText(reservation.status)}
                          </span>
                        </div>
                      </div>
                      <div className="text-right flex flex-col items-end gap-3">
                        <div className="text-2xl font-bold text-blue-600">
                          {reservation.totalPrice?.toLocaleString()}원
                        </div>
                        {isCancellable(reservation.status) &&
                          reservation.id && (
                            <Button
                              variant="destructive"
                              size="sm"
                              onClick={(e) => {
                                e.stopPropagation();
                                handleDeleteClick(reservation.id);
                              }}
                              disabled={isCancelling}
                              className="flex items-center gap-2"
                            >
                              <Trash2 className="w-4 h-4" />
                              예약 취소
                            </Button>
                          )}
                      </div>
                    </div>
                  </CardHeader>

                  <CardContent>
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                      <div className="space-y-3">
                        <div className="flex items-center gap-3">
                          <Calendar className="w-5 h-5 text-gray-500" />
                          <div>
                            <p className="text-sm text-gray-600">예약일시</p>
                            <p className="font-medium">
                              {reservation.reservationTime
                                ? format(
                                    reservation.reservationTime,
                                    "yyyy년 MM월 dd일 HH:mm"
                                  )
                                : "정보 없음"}
                            </p>
                          </div>
                        </div>

                        <div className="flex items-center gap-3">
                          <Receipt className="w-5 h-5 text-gray-500" />
                          <div>
                            <p className="text-sm text-gray-600">
                              공연 세션 ID
                            </p>
                            <p className="font-medium">
                              {reservation.performanceSessionId}
                            </p>
                          </div>
                        </div>
                      </div>

                      <div className="space-y-3">
                        {reservation.seats && reservation.seats.length > 0 && (
                          <div className="flex items-start gap-3">
                            <MapPin className="w-5 h-5 text-gray-500 mt-0.5" />
                            <div>
                              <p className="text-sm text-gray-600">좌석 정보</p>
                              <div className="space-y-1">
                                {reservation.seats.map((seat, seatIndex) => (
                                  <p
                                    key={seatIndex}
                                    className="font-medium text-sm"
                                  >
                                    {seat.areaName} {seat.row}열 {seat.number}번
                                  </p>
                                ))}
                              </div>
                            </div>
                          </div>
                        )}
                      </div>
                    </div>
                  </CardContent>
                </Card>
              );
            })}

            {/* 로딩 인디케이터 */}
            {isFetchingNextPage && (
              <div className="flex justify-center items-center py-8">
                <div className="inline-block w-6 h-6 border-4 border-blue-400 border-t-transparent rounded-full animate-spin"></div>
                <span className="ml-3 text-gray-600">
                  더 많은 예약 내역을 불러오는 중...
                </span>
              </div>
            )}

            {/* 더 이상 불러올 데이터가 없을 때 */}
            {!hasNextPage && myReservationList.length > 0 && (
              <div className="text-center py-8 text-gray-500">
                모든 예약 내역을 불러왔습니다.
              </div>
            )}
          </div>
        )}
      </div>

      {/* 삭제 확인 모달 */}
      <Dialog open={isDeleteModalOpen} onOpenChange={setIsDeleteModalOpen}>
        <DialogContent className="sm:max-w-md">
          <DialogHeader>
            <DialogTitle className="flex items-center gap-2">
              <Trash2 className="w-5 h-5 text-red-500" />
              예약 취소 확인
            </DialogTitle>
            <DialogDescription className="text-left">
              정말로 이 예약을 취소하시겠습니까?
              <br />
              <span className="text-red-600 font-medium">
                취소된 예약은 복구할 수 없습니다.
              </span>
            </DialogDescription>
          </DialogHeader>
          <DialogFooter className="flex-col sm:flex-row gap-2">
            <Button
              variant="outline"
              onClick={handleDeleteCancel}
              disabled={isCancelling}
              className="w-full sm:w-auto"
            >
              취소
            </Button>
            <Button
              variant="destructive"
              onClick={handleDeleteConfirm}
              disabled={isCancelling}
              className="w-full sm:w-auto"
            >
              {isCancelling ? (
                <>
                  <div className="inline-block w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin mr-2"></div>
                  취소 중...
                </>
              ) : (
                "예약 취소"
              )}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {selectedReservationIdForModal !== null && (
        <ReservationDetailModal
          id={selectedReservationIdForModal}
          onClose={closeDetailModal}
        />
      )}
    </PageLayout>
  );
};
