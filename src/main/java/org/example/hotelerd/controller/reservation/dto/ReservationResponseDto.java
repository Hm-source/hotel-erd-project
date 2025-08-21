package org.example.hotelerd.controller.reservation.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.hotelerd.repository.reservation.entity.ReservationStatus;
import org.example.hotelerd.repository.reservation.entity.Reservations;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ReservationResponseDto {

    private Integer reservationId;
    private String hotelName;
    private LocalDate reservationDate;
    private String roomType;
    private LocalTime checkInTime;         // Hotel의 check_in_time
    private LocalTime checkOutTime;
    private Integer totalPrice;
    private String userName;
    private ReservationStatus status;

    public static ReservationResponseDto from(Reservations reservation) {
        return ReservationResponseDto.builder()
            .reservationId(reservation.getId())
            .hotelName(reservation.getRoomType().getHotel().getName())
            .roomType(reservation.getRoomType().getType())
            .checkInTime(reservation.getRoomType().getHotel().getCheckInTime())
            .checkOutTime(reservation.getRoomType().getHotel().getCheckOutTime())
            .totalPrice(reservation.getTotalPrice())
            .userName(reservation.getUser().getName())
            .build();
    }
}
