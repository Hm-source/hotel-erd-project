package org.example.hotelerd.repository.hotel;

import jakarta.persistence.LockModeType;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.example.hotelerd.controller.hotel.dto.HotelSimpleResponseDto;
import org.example.hotelerd.repository.hotel.entity.RoomDatePrice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface RoomDatePriceRepository extends JpaRepository<RoomDatePrice, Integer> {

    @Query("""
            SELECT rdp FROM RoomDatePrice rdp
            WHERE rdp.roomType.id IN :roomTypeIds
                AND rdp.dateAvailable = :dateAvailable
            ORDER BY rdp.roomType.id
        """)
    List<RoomDatePrice> findByRoomTypeIdsAndDate(
        @Param("roomTypeIds") List<Integer> roomTypeIds,
        @Param("dateAvailable") LocalDate dateAvailable
    );

    @Query("SELECT new org.example.hotelerd.controller.hotel.dto.HotelSimpleResponseDto(" +
        "h.name, :checkDate, MIN(rdp.price), rt.type) " +
        "FROM RoomDatePrice rdp " +
        "JOIN rdp.roomType rt " +
        "JOIN rt.hotel h " +
        "WHERE rdp.dateAvailable = :checkDate AND rdp.quantity > 0 " +
        "GROUP BY h.id, h.name, rt.type " +
        "ORDER BY h.id")
    Page<HotelSimpleResponseDto> findAllCheapestHotelInfo(@Param("checkDate") LocalDate checkDate,
        Pageable pageable);

    Optional<RoomDatePrice> findByRoomTypeIdAndDateAvailable(Integer roomTypeId,
        LocalDate dateAvailable);

    @Query("SELECT rdp FROM RoomDatePrice rdp WHERE rdp.roomType.id = :roomTypeId AND rdp.dateAvailable = :date")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<RoomDatePrice> findByRoomTypeIdAndDateAvailableWithLock(
        @Param("roomTypeId") Integer roomTypeId,
        @Param("date") LocalDate dateAvailable);
}
