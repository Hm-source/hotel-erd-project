package org.example.hotelerd.repository.reservation;

import org.example.hotelerd.repository.entity.Reservations;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservations, Long> {

}