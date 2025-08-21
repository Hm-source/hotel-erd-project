package org.example.hotelerd.controller.reservation.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.hotelerd.repository.reservation.entity.Reservations;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ReservationCancelResponseDto {

    private Integer reservationId;

    public static ReservationCancelResponseDto from(
        Reservations reservation) {
        return ReservationCancelResponseDto.builder()
            .reservationId(reservation.getId())
            .build();
    }
}
