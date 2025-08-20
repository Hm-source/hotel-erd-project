package org.example.hotelerd.controller.hotel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hotelerd.repository.hotel.entity.RoomDatePrice;
import org.example.hotelerd.repository.hotel.entity.RoomType;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomAvailabilityInfoDto {

    private Integer roomTypeId;
    private String roomType;
    private String description;
    private Integer price;
    private Integer availableRoomCount;

    public static RoomAvailabilityInfoDto of(RoomType roomType, RoomDatePrice roomDatePrice) {
        return RoomAvailabilityInfoDto.builder()
            .roomTypeId(roomType.getId())
            .roomType(roomType.getType())
            .description(roomType.getDescription())
            .price(roomDatePrice.getPrice())
            .availableRoomCount(roomDatePrice.getQuantity())
            .build();
    }
}
