package org.example.hotelerd.controller.hotel.dto;

import java.util.Optional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.hotelerd.repository.hotel.entity.RoomDatePrice;
import org.example.hotelerd.repository.hotel.entity.RoomType;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class RoomAvailabilityInfoDto {

    private Integer roomTypeId;
    private String roomType;
    private String description;
    private Integer price;
    private Boolean isAvailable;
    private Integer availableRoomCount;
    private Boolean isPeakSeason;
    private Integer seasonId;
    private String seasonName;
    private Integer discountRate;
    private Integer seasonPrice;

    public static RoomAvailabilityInfoDto of(RoomType roomType, RoomDatePrice roomDatePrice) {
        boolean hasStock = roomDatePrice.getQuantity() > 0;
        RoomAvailabilityInfoDtoBuilder builder = RoomAvailabilityInfoDto.builder()
            .roomTypeId(roomType.getId())
            .roomType(roomType.getType())
            .description(roomType.getDescription())
            .isAvailable(hasStock)
            .availableRoomCount(roomDatePrice.getQuantity());

        if (hasStock) {
            builder.price(roomDatePrice.getPrice());
        }

        Optional.ofNullable(roomDatePrice.getSeason())
            .ifPresentOrElse(
                s -> {
                    builder
                        .isPeakSeason(true)
                        .seasonId(s.getId())
                        .seasonName(s.getName())
                        .discountRate(s.getDiscountRate());

                    if (hasStock) {
                        builder.seasonPrice(
                            calculateDiscountPrice(roomDatePrice.getPrice(), s.getDiscountRate()));
                    }
                },
                () -> builder.isPeakSeason(false)
            );

        return builder.build();
    }

    private static Integer calculateDiscountPrice(Integer basePrice, Integer discountRate) {
        return Optional.ofNullable(basePrice)
            .filter(price -> discountRate != null)
            .map(price -> price * discountRate / 100)
            .orElse(null);
    }
}
