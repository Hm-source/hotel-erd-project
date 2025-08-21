package org.example.hotelerd.controller.hotel.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.hotelerd.repository.hotel.entity.Hotel;

@Getter
@Builder
@AllArgsConstructor
public class HotelDetailResponseDto {

    private Integer hotelId;
    private String hotelName;
    private String address;
    private LocalTime checkInTime;
    private LocalTime checkOutTime;
    private LocalDate requestDate;
    private List<RoomAvailabilityInfoDto> roomTypes;

    public static HotelDetailResponseDto from(Hotel hotel, LocalDate requestDate,
        List<RoomAvailabilityInfoDto> roomTypes) {
        return HotelDetailResponseDto.builder()
            .hotelId(hotel.getId())
            .hotelName(hotel.getName())
            .address(hotel.getAddress())
            .checkInTime(hotel.getCheckInTime())
            .checkOutTime(hotel.getCheckOutTime())
            .requestDate(requestDate)
            .roomTypes(roomTypes)
            .build();
    }
}
