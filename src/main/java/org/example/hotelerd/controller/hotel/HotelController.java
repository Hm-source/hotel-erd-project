package org.example.hotelerd.controller.hotel;


import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.hotelerd.controller.hotel.dto.HotelDetailResponseDto;
import org.example.hotelerd.service.HotelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hotels")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class HotelController {

    HotelService hotelService;

    /*
    - 단일 호텔 상세 정보 조회 API - 김효민
    - 요청 : 날짜 + 호텔아이디
    - 반환 : 호텔의 상세 정보와 해당 날짜에 모든 방의 예약가능 상태와 가격을 보여준다
        - 날짜에 따라서 성수기 혹은 비성수기 가격을 표기해야한다
        - 해당 날짜의 객실타입마다 예약가능한지 여부를 표기해준다
        - 해당 날짜에 예약불가능한 객실타입이더라도 예약불가능 표기와 함께 정보는 반환해야한다
            - 예약이 가능하지 않기 때문에 객실타입표기 시 가격만 표기되지 않으면되기때문
     */
    @GetMapping("/{id}")
    public ResponseEntity<HotelDetailResponseDto> hotelDetails(@PathVariable Integer id,
        @RequestParam LocalDate date) {
        HotelDetailResponseDto response = hotelService.getHotelDetail(id,
            date);
        return ResponseEntity.ok(response);
    }

}
