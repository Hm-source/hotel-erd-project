package org.example.hotelerd.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final UserRepository userRepository;
    private final UserRepository roomTypeRepository;
    private final RoomDatePriceRepository roomDatePriceRepository;
    private final ReservationRepository reservationRepository;

    @Transactional
    public ReservationResponseDto createReservation(ReservationRequestDto requestDto) {
        // 유저 및 객실 타입 정보 조회
        User user = userRepository.findById(requestDto.userId())
                .orElseThrow(() -> new EntityNotFoundException("해당 유저를 찾을 수 없음" + requestDto.userId()));
        RoomType roomType = roomTypeRepository.findById(requestDto.roomTypeId())
                .orElseThrow(() -> new EntityNotFoundException("해당 객실 타입을 찾을 수 없음." + requestDto.roomTypeId()));

        // 해당 날짜의 객실 재고 정보 조회 및 확인
        RoomDatePrice roomInventory = roomDatePriceRepository.findByRoomTypeAndDate(roomType, requestDto.stayDate())
                .orElseThrow(() -> new IllegalStateException("해당 날짜에 예약 가능한 상품이 없음."));

        if (roomInventory.getQuantity() <= 0) {
            throw new IllegalStateException("해당 객실은 모두 예약되었음.");
        }

        // 예약 되면 재고 1개 감소
        roomInventory.decreaseQuantity();
        roomDatePriceRepository.save(roomInventory);

        // 예약 정보
        Reservation newReservation = Reservation.builder()
                .user(user)
                .roomType(roomType)
                .room(null)         // 방은 아직 배정 되지 않아서 null
                .checkIn(requestDto.stayDate())
                .checkOut(requestDto.stayDate().plusDays(1))
                .status(ReservationStatus.CONFIRMED.name())
                .totalPrice(roomInventory.getPrice().intValue())
                .build();
        reservationRepository.save(newReservation);

        return ReservationResponseDto.fromEntity(newReservation);
    }
}
