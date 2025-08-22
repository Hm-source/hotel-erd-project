package org.example.hotelerd.service;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.hotelerd.controller.reservation.dto.ReservationCancelResponseDto;
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
                () -> new NoSuchElementException(
                    "해당 유저를 찾을 수 없습니다. userId : " + requestDto.getUserId()));
        RoomType roomType = roomTypeRepository.findById(requestDto.getRoomTypeId())
            .orElseThrow(
                () -> new NoSuchElementException(
                    "해당 객실 타입을 찾을 수 없습니다. roomTypeId : " + requestDto.getRoomTypeId()));

        RoomDatePrice roomInventory = roomDatePriceRepository.findByRoomTypeIdAndDateAvailableWithLock(
                roomType.getId(),
                requestDto.getStayDate())
            .orElseThrow(() -> new NoSuchElementException(
                "해당 날짜에 예약 가능한 상품이 없습니다. date : " + requestDto.getStayDate()));

        roomInventory.decreaseQuantity();

        Reservations reservation = Reservations.builder()
            .user(user)
            .roomType(roomType)
            .room(null)
            .roomDatePrice(roomInventory)
            .status(ReservationStatus.CONFIRMED)
            .totalPrice(calculateTotalPrice(roomInventory))
            .build();
        reservationRepository.save(reservation);

        return ReservationResponseDto.from(reservation);
    }

    @Transactional
    public ReservationCancelResponseDto cancelReservation(Integer reservationId, Integer userId) {
        Reservations reservation = reservationRepository.findById(reservationId)
            .orElseThrow(
                () -> new NoSuchElementException("해당 예약을 찾을 수 없습니다. 예약ID: " + reservationId));

        Users user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("해당 사용자를 찾을 수 없습니다. userId : " + userId));

        reservation.validateCancelPermission(user.getId());
        reservation.validateCancellable();

        LocalDate reservationDate = reservation.getRoomDatePrice().getDateAvailable();

        RoomDatePrice roomDatePrice = roomDatePriceRepository
            .findByRoomTypeIdAndDateAvailableWithLock(
                reservation.getRoomType().getId(),
                reservationDate)
            .orElseThrow(() -> new NoSuchElementException("재고 정보를 찾을 수 없습니다."));

        reservation.cancel();
        roomDatePrice.increaseQuantity();

        return ReservationCancelResponseDto.from(reservation);
    }

    private Integer calculateTotalPrice(RoomDatePrice roomInventory) {
        Integer basePrice = roomInventory.getPrice();

        if (roomInventory.getSeason() == null) {
            return basePrice;
        } else {
            Integer discountRate = roomInventory.getSeason().getDiscountRate();
            return basePrice * discountRate / 100;
        }
    }
}
