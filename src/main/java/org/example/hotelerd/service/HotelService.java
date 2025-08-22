package org.example.hotelerd.service;


import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public Page<HotelSimpleResponseDto> getHotelInfo(LocalDate checkDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return roomDatePriceRepository.findAllCheapestHotelInfo(checkDate, pageable);
    }


    @Transactional(readOnly = true)
    public HotelDetailResponseDto getHotelDetail(Integer hotelId, LocalDate checkDate) {
        Hotel hotel = hotelRepository.findById(hotelId)
            .orElseThrow(() -> new RuntimeException("호텔을 찾을 수 없습니다. hotelId: " + hotelId));

        List<RoomType> roomTypes = hotel.getRoomTypes();

        log.info(roomTypes.stream()
            .map(RoomType::toString)
            .collect(Collectors.joining(",\n ")));

        List<Integer> roomTypeIds = roomTypes.stream()
            .map(RoomType::getId)
            .toList();

        List<RoomDatePrice> roomDatePrices = roomDatePriceRepository.findByRoomTypeIdsAndDate(
            roomTypeIds, checkDate);

        List<RoomAvailabilityInfoDto> roomAvailabilityInfoDtos = roomDatePrices.stream()
            .map(room -> {
                return RoomAvailabilityInfoDto.of(room.getRoomType(), room);
            })
            .toList();

        return HotelDetailResponseDto.from(
            hotel,
            checkDate,
            roomAvailabilityInfoDtos
        );
    }

}
