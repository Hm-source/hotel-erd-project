package org.example.hotelerd.repository.reservation;

import org.example.hotelerd.repository.reservation.entity.Reservations;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservations, Long> {

}