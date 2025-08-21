package org.example.hotelerd.controller.hotel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hotelerd.repository.hotel.entity.RoomDatePrice;
import org.example.hotelerd.repository.hotel.entity.RoomType;
import org.example.hotelerd.repository.hotel.entity.Season;

@Getter
@NoArgsConstructor
@AllArgsConstructor
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
        Season season = roomDatePrice.getSeason();
        return RoomAvailabilityInfoDto.builder()
            .roomTypeId(roomType.getId())
            .roomType(roomType.getType())
            .description(roomType.getDescription())
            .price(hasStock ? roomDatePrice.getPrice() : null)
            .isAvailable(hasStock)
            .availableRoomCount(roomDatePrice.getQuantity())
            .isPeakSeason(season != null)
            .seasonId(season != null ? season.getId() : null)
            .seasonName(season != null ? season.getName() : null)
            .seasonPrice(season != null ?
                (hasStock ? calculateDiscountPrice(roomDatePrice.getPrice(),
                    season.getDiscountRate()) : null) : null)
            .discountRate(season != null ? season.getDiscountRate() : null)
            .build();
    }

    public static RoomAvailabilityInfoDto unavailable(RoomType roomType,
        RoomDatePrice roomDatePrice) {
        Season season = roomDatePrice.getSeason();
        return RoomAvailabilityInfoDto.builder()
            .roomTypeId(roomType.getId())
            .roomType(roomType.getType())
            .description(roomType.getDescription())
            .isAvailable(false)
            .price(null)
            .availableRoomCount(0)
            .isPeakSeason(season != null)
            .seasonId(season != null ? season.getId() : null)
            .seasonName(season != null ? season.getName() : null)
            .seasonPrice(null)
            .discountRate(null)
            .build();
    }

    public static RoomAvailabilityInfoDto available(RoomType roomType,
        RoomDatePrice roomDatePrice) {
        Season season = roomDatePrice.getSeason();
        return RoomAvailabilityInfoDto.builder()
            .roomTypeId(roomType.getId())
            .roomType(roomType.getType())
            .description(roomType.getDescription())
            .isAvailable(true)
            .price(roomDatePrice.getPrice())
            .availableRoomCount(roomDatePrice.getQuantity())
            .isPeakSeason(season != null)
            .seasonId(season != null ? season.getId() : null)
            .seasonName(season != null ? season.getName() : null)
            .seasonPrice(season != null ?
                calculateDiscountPrice(roomDatePrice.getPrice(),
                    season.getDiscountRate()) : null)
            .discountRate(season != null ? season.getDiscountRate() : null)
            .build();
    }

    private static Integer calculateDiscountPrice(Integer basePrice, Integer discountRate) {
        if (basePrice == null || discountRate == null) {
            return null;
        }
        return basePrice / 100 * discountRate;
    }
}
