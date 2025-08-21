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
import org.example.hotelerd.repository.hotel.RoomTypeRepository;
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
    RoomTypeRepository roomTypeRepository;
    RoomDatePriceRepository roomDatePriceRepository;





    @Transactional(readOnly = true)
    public List<HotelSimpleResponseDto> getHotelInfo(LocalDate checkDate) {
        List<Hotel> hotels = hotelRepository.findAll();

        return hotels.stream()
                .map(hotel -> {
                    // findCheapestAvailableRoom 메서드에 hotelId 전달
                    Optional<RoomDatePrice> cheapestRoomDatePrice = roomDatePriceRepository.findCheapestAvailableRoom(hotel.getId());

                    Integer cheapestRoomPrice = null;
                    String cheapRoomTypeName = null;

                    if (cheapestRoomDatePrice.isPresent()) {
                        RoomType roomType = cheapestRoomDatePrice.get().getRoomType();
                        cheapestRoomPrice = cheapestRoomDatePrice.get().getPrice();
                        cheapRoomTypeName = roomType.getType(); 
                    }

                    return HotelSimpleResponseDto.from(hotel, checkDate, cheapestRoomPrice, cheapRoomTypeName);
                })
                .collect(Collectors.toList());
    }





    @Transactional(readOnly = true)
    public HotelDetailResponseDto getHotelDetail(Integer hotelId, LocalDate checkDate) {
        log.info("호텔 상세 정보 조회 시작 - hotelId: {}, checkDate: {}", hotelId, checkDate);

        Hotel hotel = hotelRepository.findById(hotelId)
            .orElseThrow(() -> new RuntimeException("호텔을 찾을 수 없습니다. hotelId: " + hotelId));
        log.info("호텔 조회 완료:");
        log.info("  - 호텔 ID: {}", hotel.getId());
        log.info("  - 호텔명: {}", hotel.getName());
        log.info("  - 주소: {}", hotel.getAddress());
        log.info("  - 체크인: {}, 체크아웃: {}", hotel.getCheckInTime(), hotel.getCheckOutTime());

        List<RoomType> roomTypes = roomTypeRepository.findByHotelId(hotelId);
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
        List<RoomAvailabilityInfoDto> roomAvailabilityInfoDtos = roomTypes.stream()
            .map(roomType -> {
                Integer roomTypeId = roomType.getId();
                RoomDatePrice roomDatePrice = roomDatePrices.stream()
                    .filter(rdp -> rdp.getRoomType().getId().equals(roomTypeId))
                    .findFirst()
                    .orElse(null);

                if (roomDatePrice == null) {
                    log.debug("객실타입 {}에 대한 가격정보가 없어 제외됩니다", roomType.getType());
                    return null; // null 반환
                }

                return RoomAvailabilityInfoDto.of(roomType, roomDatePrice);
            })
            .filter(dto -> dto != null) // null 필터링
            .toList();
        return HotelDetailResponseDto.from(
            hotel,
            checkDate,
            roomAvailabilityInfoDtos
        );
    }

}
