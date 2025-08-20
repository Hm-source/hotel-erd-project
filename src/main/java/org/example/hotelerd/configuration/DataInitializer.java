package org.example.hotelerd.configuration;


import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hotelerd.repository.hotel.entity.Hotel;
import org.example.hotelerd.repository.hotel.entity.Room;
import org.example.hotelerd.repository.hotel.entity.RoomDatePrice;
import org.example.hotelerd.repository.hotel.entity.RoomType;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final EntityManager entityManager;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initializeData() {
        log.info("데이터 초기화 시작");

        // 기존 데이터가 있는지 확인
        Long hotelCount = entityManager.createQuery("SELECT COUNT(h) FROM Hotel h", Long.class)
            .getSingleResult();

        if (hotelCount > 0) {
            log.info("이미 데이터가 존재합니다. 초기화를 건너뜁니다.");
            return;
        }

        try {
            // 1. 호텔 데이터 생성
            List<Hotel> hotels = createHotels();
            hotels.forEach(entityManager::persist);
            entityManager.flush(); // 호텔 ID 생성을 위해 flush

            // 2. 객실타입 데이터 생성
            List<RoomType> roomTypes = createRoomTypes(hotels);
            roomTypes.forEach(entityManager::persist);
            entityManager.flush(); // 객실타입 ID 생성을 위해 flush

            // 3. 방 데이터 생성
            List<Room> rooms = createRooms(roomTypes);
            rooms.forEach(entityManager::persist);
            entityManager.flush();

            // 4. 가격 데이터 생성
            List<RoomDatePrice> roomDatePrices = createRoomDatePrices(roomTypes);
            roomDatePrices.forEach(entityManager::persist);

            log.info("데이터 초기화 완료 - 호텔: {}개, 객실타입: {}개, 방: {}개, 가격정보: {}개",
                hotels.size(), roomTypes.size(), rooms.size(), roomDatePrices.size());

        } catch (Exception e) {
            log.error("데이터 초기화 중 오류 발생", e);
            throw e;
        }
    }

    private List<Hotel> createHotels() {
        return List.of(
            new Hotel(null, "그랜드 서울 호텔", "서울시 중구 을지로 176",
                LocalTime.of(15, 0), LocalTime.of(11, 0)),
            new Hotel(null, "부산 오션뷰 호텔", "부산시 해운대구 마린시티2로 38",
                LocalTime.of(15, 0), LocalTime.of(11, 0)),
            new Hotel(null, "제주 리조트 호텔", "제주시 애월읍 곽지리 1235",
                LocalTime.of(16, 0), LocalTime.of(11, 0)),
            new Hotel(null, "강릉 스카이 호텔", "강릉시 경포로 365",
                LocalTime.of(15, 0), LocalTime.of(11, 0)),
            new Hotel(null, "대구 시티 호텔", "대구시 수성구 동대구로 123",
                LocalTime.of(15, 0), LocalTime.of(11, 0))
        );
    }

    private List<RoomType> createRoomTypes(List<Hotel> hotels) {
        return List.of(
            // 그랜드 서울 호텔
            new RoomType(null, "스탠다드", hotels.get(0), "기본형 객실 (더블베드 1개)"),
            new RoomType(null, "디럭스", hotels.get(0), "고급형 객실 (킹베드 1개, 소파)"),
            new RoomType(null, "스위트", hotels.get(0), "특실 (킹베드 1개, 거실, 미니바)"),

            // 부산 오션뷰 호텔
            new RoomType(null, "오션뷰", hotels.get(1), "바다전망 객실 (더블베드 1개)"),
            new RoomType(null, "시티뷰", hotels.get(1), "도시전망 객실 (더블베드 1개)"),
            new RoomType(null, "패밀리", hotels.get(1), "가족형 객실 (더블베드 2개)"),

            // 제주 리조트 호텔
            new RoomType(null, "가든뷰", hotels.get(2), "정원전망 객실 (더블베드 1개)"),
            new RoomType(null, "풀뷰", hotels.get(2), "수영장전망 객실 (킹베드 1개)"),
            new RoomType(null, "펜트하우스", hotels.get(2), "최고급 객실 (킹베드 1개, 테라스)"),

            // 강릉 스카이 호텔
            new RoomType(null, "스탠다드", hotels.get(3), "기본형 객실 (더블베드 1개)"),
            new RoomType(null, "디럭스", hotels.get(3), "고급형 객실 (킹베드 1개)"),

            // 대구 시티 호텔
            new RoomType(null, "이코노미", hotels.get(4), "경제형 객실 (싱글베드 1개)"),
            new RoomType(null, "비즈니스", hotels.get(4), "비즈니스 객실 (더블베드 1개, 업무공간)")
        );
    }

    private List<Room> createRooms(List<RoomType> roomTypes) {
        return List.of(
            // 각 객실타입마다 3-5개의 방 생성
            // 그랜드 서울 호텔 - 스탠다드 (5개)
            new Room(null, roomTypes.get(0)),
            new Room(null, roomTypes.get(0)),
            new Room(null, roomTypes.get(0)),
            new Room(null, roomTypes.get(0)),
            new Room(null, roomTypes.get(0)),

            // 그랜드 서울 호텔 - 디럭스 (3개)
            new Room(null, roomTypes.get(1)),
            new Room(null, roomTypes.get(1)),
            new Room(null, roomTypes.get(1)),

            // 그랜드 서울 호텔 - 스위트 (2개)
            new Room(null, roomTypes.get(2)),
            new Room(null, roomTypes.get(2)),

            // 부산 오션뷰 호텔 - 오션뷰 (4개)
            new Room(null, roomTypes.get(3)),
            new Room(null, roomTypes.get(3)),
            new Room(null, roomTypes.get(3)),
            new Room(null, roomTypes.get(3)),

            // 부산 오션뷰 호텔 - 시티뷰 (3개)
            new Room(null, roomTypes.get(4)),
            new Room(null, roomTypes.get(4)),
            new Room(null, roomTypes.get(4)),

            // 부산 오션뷰 호텔 - 패밀리 (2개)
            new Room(null, roomTypes.get(5)),
            new Room(null, roomTypes.get(5)),

            // 제주 리조트 호텔 - 가든뷰 (4개)
            new Room(null, roomTypes.get(6)),
            new Room(null, roomTypes.get(6)),
            new Room(null, roomTypes.get(6)),
            new Room(null, roomTypes.get(6)),

            // 제주 리조트 호텔 - 풀뷰 (3개)
            new Room(null, roomTypes.get(7)),
            new Room(null, roomTypes.get(7)),
            new Room(null, roomTypes.get(7)),

            // 제주 리조트 호텔 - 펜트하우스 (1개)
            new Room(null, roomTypes.get(8)),

            // 강릉 스카이 호텔 - 스탠다드 (4개)
            new Room(null, roomTypes.get(9)),
            new Room(null, roomTypes.get(9)),
            new Room(null, roomTypes.get(9)),
            new Room(null, roomTypes.get(9)),

            // 강릉 스카이 호텔 - 디럭스 (2개)
            new Room(null, roomTypes.get(10)),
            new Room(null, roomTypes.get(10)),

            // 대구 시티 호텔 - 이코노미 (5개)
            new Room(null, roomTypes.get(11)),
            new Room(null, roomTypes.get(11)),
            new Room(null, roomTypes.get(11)),
            new Room(null, roomTypes.get(11)),
            new Room(null, roomTypes.get(11)),

            // 대구 시티 호텔 - 비즈니스 (3개)
            new Room(null, roomTypes.get(12)),
            new Room(null, roomTypes.get(12)),
            new Room(null, roomTypes.get(12))
        );
    }

    private List<RoomDatePrice> createRoomDatePrices(List<RoomType> roomTypes) {
        LocalDate startDate = LocalDate.now().plusDays(1); // 내일부터
        LocalDate endDate = startDate.plusDays(30); // 30일간

        List<RoomDatePrice> prices = new java.util.ArrayList<>();

        // 각 객실타입별로 30일간의 가격 데이터 생성
        for (RoomType roomType : roomTypes) {
            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                Integer basePrice = getBasePriceForRoomType(roomType);
                prices.add(new RoomDatePrice(null, roomType, date, basePrice, 1));
            }
        }

        return prices;
    }

    private Integer getBasePriceForRoomType(RoomType roomType) {
        // 호텔별, 객실타입별 기본 가격 설정
        String hotelName = roomType.getHotel().getName();
        String roomTypeName = roomType.getType();

        if (hotelName.contains("그랜드 서울")) {
            return switch (roomTypeName) {
                case "스탠다드" -> 150000;
                case "디럭스" -> 220000;
                case "스위트" -> 350000;
                default -> 150000;
            };
        } else if (hotelName.contains("부산 오션뷰")) {
            return switch (roomTypeName) {
                case "오션뷰" -> 180000;
                case "시티뷰" -> 130000;
                case "패밀리" -> 250000;
                default -> 130000;
            };
        } else if (hotelName.contains("제주 리조트")) {
            return switch (roomTypeName) {
                case "가든뷰" -> 200000;
                case "풀뷰" -> 280000;
                case "펜트하우스" -> 500000;
                default -> 200000;
            };
        } else if (hotelName.contains("강릉 스카이")) {
            return switch (roomTypeName) {
                case "스탠다드" -> 120000;
                case "디럭스" -> 170000;
                default -> 120000;
            };
        } else if (hotelName.contains("대구 시티")) {
            return switch (roomTypeName) {
                case "이코노미" -> 80000;
                case "비즈니스" -> 110000;
                default -> 80000;
            };
        }

        return 100000; // 기본값
    }
}