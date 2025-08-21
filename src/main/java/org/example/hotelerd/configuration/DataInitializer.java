package org.example.hotelerd.configuration;

import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
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

            // 4. 가격 데이터 생성 (quantity 계산 포함)
            List<RoomDatePrice> roomDatePrices = createRoomDatePrices(roomTypes, rooms);
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
                LocalTime.of(15, 0), LocalTime.of(11, 0), new ArrayList<>()),
            new Hotel(null, "부산 오션뷰 호텔", "부산시 해운대구 마린시티2로 38",
                LocalTime.of(15, 0), LocalTime.of(11, 0), new ArrayList<>()),
            new Hotel(null, "제주 리조트 호텔", "제주시 애월읍 곽지리 1235",
                LocalTime.of(16, 0), LocalTime.of(11, 0), new ArrayList<>()),
            new Hotel(null, "강릉 스카이 호텔", "강릉시 경포로 365",
                LocalTime.of(15, 0), LocalTime.of(11, 0), new ArrayList<>()),
            new Hotel(null, "대구 시티 호텔", "대구시 수성구 동대구로 123",
                LocalTime.of(15, 0), LocalTime.of(11, 0), new ArrayList<>())
        );
    }

    private List<RoomType> createRoomTypes(List<Hotel> hotels) {
        return List.of(
            // 그랜드 서울 호텔
            new RoomType(null, "스탠다드", hotels.get(0), "기본형 객실 (더블베드 1개)", 2, 2),
            new RoomType(null, "디럭스", hotels.get(0), "고급형 객실 (킹베드 1개, 소파)", 2, 3),
            new RoomType(null, "스위트", hotels.get(0), "특실 (킹베드 1개, 거실, 미니바)", 2, 4),

            // 부산 오션뷰 호텔
            new RoomType(null, "오션뷰", hotels.get(1), "바다전망 객실 (더블베드 1개)", 2, 2),
            new RoomType(null, "시티뷰", hotels.get(1), "도시전망 객실 (더블베드 1개)", 2, 2),
            new RoomType(null, "패밀리", hotels.get(1), "가족형 객실 (더블베드 2개)", 4, 6),

            // 제주 리조트 호텔
            new RoomType(null, "가든뷰", hotels.get(2), "정원전망 객실 (더블베드 1개)", 2, 2),
            new RoomType(null, "풀뷰", hotels.get(2), "수영장전망 객실 (킹베드 1개)", 2, 3),
            new RoomType(null, "펜트하우스", hotels.get(2), "최고급 객실 (킹베드 1개, 테라스)", 2, 4),

            // 강릉 스카이 호텔
            new RoomType(null, "스탠다드", hotels.get(3), "기본형 객실 (더블베드 1개)", 2, 4),
            new RoomType(null, "디럭스", hotels.get(3), "고급형 객실 (킹베드 1개)", 2, 3),

            // 대구 시티 호텔
            new RoomType(null, "이코노미", hotels.get(4), "경제형 객실 (싱글베드 1개)", 1, 1),
            new RoomType(null, "비즈니스", hotels.get(4), "비즈니스 객실 (더블베드 1개, 업무공간)", 2, 2)
        );
    }

    private List<Room> createRooms(List<RoomType> roomTypes) {
        List<Room> rooms = new ArrayList<>();
        int roomNumber = 101; // 시작 호실 번호

        // 각 객실타입별 방 개수 설정
        int[] roomCounts = {0, 3, 2, 4, 3, 2, 4, 3, 1, 4, 0, 5, 3};

        for (int i = 0; i < roomTypes.size(); i++) {
            RoomType roomType = roomTypes.get(i);
            int count = roomCounts[i];

            for (int j = 0; j < count; j++) {
                rooms.add(new Room(null, roomNumber++, roomType));
            }

            log.debug("{}의 {} 객실타입: {}개 방 생성",
                roomType.getHotel().getName(), roomType.getType(), count);
        }

        return rooms;
    }

    private List<RoomDatePrice> createRoomDatePrices(List<RoomType> roomTypes, List<Room> rooms) {
        LocalDate startDate = LocalDate.now().plusDays(1); // 내일부터
        LocalDate endDate = startDate.plusDays(60); // 60일간

        List<RoomDatePrice> prices = new ArrayList<>();

        // 각 객실타입별 방 개수 계산
        for (RoomType roomType : roomTypes) {
            // 해당 객실타입의 방 개수 계산
            long roomCount = rooms.stream()
                .filter(room -> room.getRoomType().getId().equals(roomType.getId()))
                .count();

            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                Integer basePrice = getBasePriceForRoomType(roomType);

                // 시즌가 적용
                Integer finalPrice = applySeasonalPricing(basePrice, date);

                // quantity는 해당 객실타입의 총 방 개수
                prices.add(new RoomDatePrice(null, roomType, date, finalPrice, (int) roomCount));
            }

            log.debug("{}의 {} 객실타입: {}개 방, 60일간 가격 정보 생성",
                roomType.getHotel().getName(), roomType.getType(), roomCount);
        }

        return prices;
    }

    private Integer getBasePriceForRoomType(RoomType roomType) {
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

    /**
     * 시즌가 및 특별가격 적용
     */
    private Integer applySeasonalPricing(Integer basePrice, LocalDate date) {
        double multiplier = 1.0;

        // 성수기 적용 (여름: 7-8월, 겨울: 12-2월)
        int month = date.getMonthValue();
        if (month == 7 || month == 8) {
            multiplier *= 1.5; // 여름 성수기 50% 할증
            log.trace("여름 성수기 적용: {}", date);
        } else if (month == 12 || month == 1 || month == 2) {
            multiplier *= 1.3; // 겨울 성수기 30% 할증
            log.trace("겨울 성수기 적용: {}", date);
        }

        // 주말 할증 (금, 토, 일)
        if (date.getDayOfWeek().getValue() >= 5) {
            multiplier *= 1.2; // 주말 20% 할증
            log.trace("주말 할증 적용: {}", date);
        }

        // 연휴 할증 (크리스마스, 신정)
        if ((month == 12 && date.getDayOfMonth() >= 24) ||
            (month == 1 && date.getDayOfMonth() <= 3)) {
            multiplier *= 1.4; // 연휴 40% 할증
            log.trace("연휴 할증 적용: {}", date);
        }

        return (int) (basePrice * multiplier);
    }
}