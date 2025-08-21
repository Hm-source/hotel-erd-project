package org.example.hotelerd.repository.hotel;

import java.util.Optional;
import org.example.hotelerd.repository.hotel.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Integer> {

    Optional<Hotel> findById(Integer id);

}
