package org.example.hotelerd.repository.reservation;

import java.util.Optional;
import org.example.hotelerd.repository.reservation.entity.Reservations;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservations, Integer> {

    Optional<Reservations> findById(Integer id);

    Reservations save(Reservations entity);

}