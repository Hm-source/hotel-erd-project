package org.example.hotelerd.repository.hotel;

import java.util.List;
import java.util.Optional;
import org.example.hotelerd.repository.hotel.entity.Hotel;
import org.example.hotelerd.repository.hotel.entity.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Integer> {

    Optional<Hotel> findById(Long id);

    @Query("SELECT rt FROM RoomType rt WHERE rt.hotel.id = :hotelId ORDER BY rt.id")
    List<RoomType> findRoomTypesById(@Param("hotelId") Long hotelId);
}
