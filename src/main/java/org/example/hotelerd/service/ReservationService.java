package org.example.hotelerd.service;

import java.util.NoSuchElementException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.hotelerd.controller.reservation.dto.ReservationRequestDto;
import org.example.hotelerd.controller.reservation.dto.ReservationResponseDto;
import org.example.hotelerd.repository.hotel.RoomDatePriceRepository;
import org.example.hotelerd.repository.hotel.RoomTypeRepository;
import org.example.hotelerd.repository.hotel.entity.RoomDatePrice;
import org.example.hotelerd.repository.hotel.entity.RoomType;
import org.example.hotelerd.repository.reservation.ReservationRepository;
import org.example.hotelerd.repository.reservation.entity.ReservationStatus;
import org.example.hotelerd.repository.reservation.entity.Reservations;
import org.example.hotelerd.repository.user.UserRepository;
import org.example.hotelerd.repository.user.entity.Users;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ReservationService {

    UserRepository userRepository;
    RoomTypeRepository roomTypeRepository;
    RoomDatePriceRepository roomDatePriceRepository;
    ReservationRepository reservationRepository;

    @Transactional
    public ReservationResponseDto createReservation(ReservationRequestDto requestDto) {
        // 유저 및 객실 타입 정보 조회
        Users user = userRepository.findById(requestDto.getUserId())
            .orElseThrow(
                () -> new NoSuchElementException("해당 유저를 찾을 수 없음" + requestDto.getUserId()));
        RoomType roomType = roomTypeRepository.findById(requestDto.getRoomTypeId())
            .orElseThrow(
                () -> new NoSuchElementException(
                    "해당 객실 타입을 찾을 수 없음." + requestDto.getRoomTypeId()));

        // 해당 날짜의 객실 재고 정보 조회 및 확인
        RoomDatePrice roomInventory = roomDatePriceRepository.findByRoomTypeIdAndDateAvailableWithLock(
                roomType.getId(),
                requestDto.getStayDate())
            .orElseThrow(() -> new NoSuchElementException("해당 날짜에 예약 가능한 상품이 없음."));

        if (roomInventory.getQuantity() <= 0) {
            throw new IllegalStateException("해당 객실은 모두 예약되었음.");
        }

        // 예약 되면 재고 1개 감소
        roomInventory.decreaseQuantity();
        roomDatePriceRepository.save(roomInventory);

        // 예약 정보
        Reservations reservation = Reservations.builder()
            .user(user)
            .roomType(roomType)
            .room(null)
            .roomDatePrice(roomInventory)
            .status(ReservationStatus.CONFIRMED)
            .totalPrice(roomInventory.getSeason() == null ? roomInventory.getPrice()
                : calculateTotalPrice(roomInventory))
            .build();
        reservationRepository.save(reservation);

        return ReservationResponseDto.from(reservation);
    }


    private Integer calculateTotalPrice(RoomDatePrice roomInventory) {
        Integer basePrice = roomInventory.getPrice();

        if (roomInventory.getSeason() == null) {
            return basePrice;
        } else {
            Integer discountRate = roomInventory.getSeason().getDiscountRate();
            return basePrice / 100 * discountRate;
        }
    }
}
