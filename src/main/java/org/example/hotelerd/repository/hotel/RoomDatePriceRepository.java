package org.example.hotelerd.repository.hotel;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.example.hotelerd.repository.hotel.entity.RoomDatePrice;
import org.springframework.data.jpa.repository.JpaRepository;
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
           "h.name, :checkDate, rdp.price, rt.type) " +
           "FROM RoomDatePrice rdp " +
           "JOIN rdp.roomType rt " +
           "JOIN rt.hotel h " +
           "WHERE rdp.dateAvailable = :checkDate AND rdp.quantity > 0 " +
           "AND rdp.price = (" +
           "    SELECT MIN(rdp2.price) FROM RoomDatePrice rdp2 " +
           "    JOIN rdp2.roomType rt2 " +
           "    JOIN rt2.hotel h2 " +
           "    WHERE h2.id = h.id AND rdp2.dateAvailable = :checkDate AND rdp2.quantity > 0" +
           ")")
    List<HotelSimpleResponseDto> findAllCheapestHotelInfo(@Param("checkDate") LocalDate checkDate);
}
