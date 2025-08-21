package org.example.hotelerd.service;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.example.hotelerd.controller.hotel.dto.HotelDetailResponseDto;
import org.example.hotelerd.controller.hotel.dto.HotelSimpleResponseDto;
import org.example.hotelerd.controller.hotel.dto.RoomAvailabilityInfoDto;
import org.example.hotelerd.repository.hotel.HotelRepository;
import org.example.hotelerd.repository.hotel.RoomDatePriceRepository;
import org.example.hotelerd.repository.hotel.entity.Hotel;
import org.example.hotelerd.repository.hotel.entity.RoomDatePrice;
import org.example.hotelerd.repository.hotel.entity.RoomType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class HotelService {

    HotelRepository hotelRepository;
    RoomDatePriceRepository roomDatePriceRepository;





    @Transactional(readOnly = true)
    public HotelSimpleResponseDto getHotelInfo(Integer hotelId, LocalDate checkDate){
        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(() ->
                new RuntimeException("호텔 ID:" + hotelId + "가 존재하지 않습니다."));

        Optional<RoomDatePrice> cheapestRoomDatePrice = roomDatePriceRepository.findCheapestAvailableRoom(hotelId);

        Integer cheapestRoomPrice = null;
        String cheapRoomTypeName = null;

        if (cheapestRoomDatePrice.isPresent()) {
            // roomType 객체를 얻고, 그 객체에서 ID와 이름을 가져옴
            RoomType roomType = cheapestRoomDatePrice.get().getRoomType();

            cheapestRoomPrice = cheapestRoomDatePrice.get().getPrice();
            cheapRoomTypeName = roomType.getType();

        }

        return HotelSimpleResponseDto.from(hotel, checkDate, cheapestRoomPrice, cheapRoomTypeName);
    }





    @Transactional(readOnly = true)
    public HotelDetailResponseDto getHotelDetail(Integer hotelId, LocalDate checkDate) {
        log.info("호텔 상세 정보 조회 시작 - hotelId: {}, checkDate: {}", hotelId, checkDate);

        Hotel hotel = hotelRepository.findById(hotelId)
            .orElseThrow(() -> new RuntimeException("호텔을 찾을 수 없습니다. hotelId: " + hotelId));
        log.info("호텔 조회 완료:   - 호텔 ID: {} - 호텔명: {}", hotel.getId(), hotel.getName());

        List<RoomType> roomTypes = hotel.getRoomTypes();
        for (int i = 0; i < roomTypes.size(); i++) {
            RoomType rt = roomTypes.get(i);
            log.info("  객실타입[{}]: ID={}, 타입={}, 설명={}",
                i, rt.getId(), rt.getType(), rt.getDescription());
        }
        List<Integer> roomTypeIds = roomTypes.stream()
            .map(RoomType::getId)
            .toList();
        log.info("추출된 객실타입 ID 목록: {}", roomTypeIds);

        List<RoomDatePrice> roomDatePrices = roomDatePriceRepository.findByRoomTypeIdsAndDate(
            roomTypeIds, checkDate);

        log.info("RoomDatePrice 리스트 ({} 개):", roomDatePrices.size());
        List<RoomAvailabilityInfoDto> roomAvailabilityInfoDtos = roomDatePrices.stream()
            .map(room -> {
                if (room.getQuantity() == 0) {
                    return RoomAvailabilityInfoDto.unavailable(room.getRoomType(), room);
                } else {
                    return RoomAvailabilityInfoDto.available(room.getRoomType(), room);
                }
            })
            .toList();
        return HotelDetailResponseDto.from(
            hotel,
            checkDate,
            roomAvailabilityInfoDtos
        );
    }

}
