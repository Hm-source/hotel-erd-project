package org.example.hotelerd.controller.reservation;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.hotelerd.controller.reservation.dto.ReservationCancelResponseDto;
import org.example.hotelerd.controller.reservation.dto.ReservationRequestDto;
import org.example.hotelerd.controller.reservation.dto.ReservationResponseDto;
import org.example.hotelerd.service.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ReservationController {

    ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationResponseDto> createReservation(
        @RequestBody ReservationRequestDto requestDto) {
        ReservationResponseDto responseDto = reservationService.createReservation(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<ReservationCancelResponseDto> cancelReservation(
        @PathVariable Integer reservationId,
        @RequestParam Integer userId) {
        ReservationCancelResponseDto responseDto = reservationService.cancelReservation(
            reservationId, userId);
        return ResponseEntity.ok(responseDto);
    }
}
