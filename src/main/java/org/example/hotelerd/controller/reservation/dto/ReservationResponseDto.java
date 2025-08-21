package org.example.hotelerd.controller.reservation.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReservationResponseDto {
    Long reservationId;
    String reservationName;
    String roomTypeName;
    String userName;
    LocalDate checkIn;
    LocalDate checkOut;
    ReservationStatus status;

    public static ReservationResponseDto fromEntity(Reservation reservation) {
        return new ReservationResponseDto(
                reservation.getReservation(),
                reservation.getRoomType().getHotel().getName(),
                reservation.getRoomType().getName(),
                reservation.getUser().getUsername(),
                reservation.getCheckIn(),
                reservation.getCheckOut(),
                reservation.getStatus()
        );
    }
}
