package org.example.hotelerd.controller.hotel.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.hotelerd.repository.hotel.entity.Hotel;
import org.example.hotelerd.repository.hotel.entity.RoomDatePrice;
import org.example.hotelerd.repository.hotel.entity.RoomType;

import java.time.LocalDate;


@Getter
@AllArgsConstructor
public class HotelSimpleResponseDto {
    private String name;
    private LocalDate date;
    private Integer price;
    private String type;

    public static HotelSimpleResponseDto from(Hotel hotel, LocalDate date,Integer price, String type){
        return new HotelSimpleResponseDto(
                hotel.getName(),
                date,
                price,
                type
        );
    }
}
