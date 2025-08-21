package org.example.hotelerd.controller.reservation.dto;


import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReservationRequestDto {

    private LocalDate stayDate;
    private Integer hotelId;
    private Integer roomTypeId;
    private Integer userId;
    private Integer numOfGuests;
    private String specialRequests;
    
}