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
import org.example.hotelerd.repository.hotel.entity.Season;
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
            entityManager.flush();

            // 2. 시즌 데이터 생성
            List<Season> seasons = createSeasons(hotels);
            seasons.forEach(entityManager::persist);
            entityManager.flush();

            // 3. 객실타입 데이터 생성
            List<RoomType> roomTypes = createRoomTypes(hotels);
            roomTypes.forEach(entityManager::persist);
            entityManager.flush();

            // 4. 방 데이터 생성
            List<Room> rooms = createRooms(roomTypes);
            rooms.forEach(entityManager::persist);
            entityManager.flush();

            // 5. 가격 데이터 생성 (시즌 정보 포함)
            List<RoomDatePrice> roomDatePrices = createRoomDatePrices(roomTypes, rooms, seasons);
            roomDatePrices.forEach(entityManager::persist);

            log.info("데이터 초기화 완료 - 호텔: {}개, 시즌: {}개, 객실타입: {}개, 방: {}개, 가격정보: {}개",
                hotels.size(), seasons.size(), roomTypes.size(), rooms.size(),
                roomDatePrices.size());

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

    private List<Season> createSeasons(List<Hotel> hotels) {
        List<Season> seasons = new ArrayList<>();
        LocalDate currentYear = LocalDate.now();
        int year = currentYear.getYear();

        // 각 호텔마다 시즌 정보 생성
        for (Hotel hotel : hotels) {
            // 여름 성수기 (7-8월) - 50% 할증
            seasons.add(new Season(null,
                LocalDate.of(year, 7, 1),
                LocalDate.of(year, 8, 31),
                "여름성수기",
                150, // 150% = 50% 할증
                hotel));

            // 겨울 성수기 (12-2월) - 30% 할증
            seasons.add(new Season(null,
                LocalDate.of(year, 12, 1),
                LocalDate.of(year, 12, 31),
                "겨울성수기",
                130, // 130% = 30% 할증
                hotel));

            seasons.add(new Season(null,
                LocalDate.of(year + 1, 1, 1),
                LocalDate.of(year + 1, 2, 28),
                "겨울성수기",
                130,
                hotel));

            // 연휴 시즌 (크리스마스) - 40% 할증
            seasons.add(new Season(null,
                LocalDate.of(year, 12, 24),
                LocalDate.of(year, 12, 26),
                "크리스마스",
                140, // 140% = 40% 할증
                hotel));

            // 신정 연휴 - 40% 할증
            seasons.add(new Season(null,
                LocalDate.of(year + 1, 1, 1),
                LocalDate.of(year + 1, 1, 3),
                "신정연휴",
                140,
                hotel));
        }

        return seasons;
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
        int roomNumber = 101;

        // 각 객실타입별 방 개수 설정 (일부 객실타입은 0개로 설정하여 예약 불가 상태 테스트)
        int[] roomCounts = {0, 3, 2, 4, 3, 2, 4, 3, 1, 4, 2, 0, 3}; // 이코노미는 0개

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

    private List<RoomDatePrice> createRoomDatePrices(List<RoomType> roomTypes, List<Room> rooms,
        List<Season> seasons) {
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = startDate.plusDays(60);

        List<RoomDatePrice> prices = new ArrayList<>();

        for (RoomType roomType : roomTypes) {
            // 해당 객실타입의 방 개수 계산
            long roomCount = rooms.stream()
                .filter(room -> room.getRoomType().getId().equals(roomType.getId()))
                .count();

            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                Integer basePrice = getBasePriceForRoomType(roomType);

                // 해당 날짜의 시즌 찾기
                Season applicableSeason = findApplicableSeason(seasons, roomType.getHotel(), date);

                // 시즌가 적용
                Integer finalPrice = applySeasonalPricing(basePrice, date, applicableSeason);

                // RoomDatePrice 생성 (시즌 정보 포함)
                prices.add(new RoomDatePrice(null, roomType, date, finalPrice, (int) roomCount,
                    applicableSeason));
            }

            log.debug("{}의 {} 객실타입: {}개 방, 60일간 가격 정보 생성",
                roomType.getHotel().getName(), roomType.getType(), roomCount);
        }

        return prices;
    }

    /**
     * 특정 날짜에 적용되는 시즌 찾기
     */
    private Season findApplicableSeason(List<Season> seasons, Hotel hotel, LocalDate date) {
        return seasons.stream()
            .filter(season -> season.getHotel().getId().equals(hotel.getId()))
            .filter(season -> !date.isBefore(season.getStartDate()) && !date.isAfter(
                season.getEndDate()))
            .findFirst()
            .orElse(null);
    }

    /**
     * 시즌가 적용 (Season 엔티티 활용)
     */
    private Integer applySeasonalPricing(Integer basePrice, LocalDate date, Season season) {
        double multiplier = 1.0;

        // Season 엔티티의 할증률 적용
        if (season != null) {
            multiplier = season.getDiscountRate() / 100.0;
            log.trace("시즌 할증 적용: {} - {} ({}%)", date, season.getName(), season.getDiscountRate());
        }

        return (int) (basePrice * multiplier);
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

        return 100000;
    }
}