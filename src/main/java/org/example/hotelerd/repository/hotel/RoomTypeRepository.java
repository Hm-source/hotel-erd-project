package org.example.hotelerd.repository.hotel;

import java.util.List;
import org.example.hotelerd.repository.hotel.entity.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomTypeRepository extends JpaRepository<RoomType, Integer> {

    @Query("SELECT rt FROM RoomType rt WHERE rt.hotel.id = :hotelId ORDER BY rt.id")
    List<RoomType> findByHotelId(@Param("hotelId") Integer hotelId);
}
