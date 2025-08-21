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

    @Query("SELECT r FROM RoomDatePrice r WHERE r.quantity > 0 AND r.roomType.hotel.id = :hotelId ORDER BY r.price ASC LIMIT 1")
    Optional<RoomDatePrice> findCheapestAvailableRoom(@Param("hotelId") Integer hotelId);
}
