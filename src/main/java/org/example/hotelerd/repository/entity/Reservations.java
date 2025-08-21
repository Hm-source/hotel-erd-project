package org.example.hotelerd.repository.reservation.entity;

import org.example.hotelerd.repository.room.entity.Room;
import org.example.hotelerd.repository.room.entity.RoomType;
import org.example.hotelerd.repository.user.entity.Users;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservations {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    // 예약 시점에는 '어떤 종류의 방'인지 기록
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_type_id", nullable = false)
    private RoomType roomType;

    // 실제 방은 체크인 시 배정되므로, 생성 시점에는 null 허용
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @Column(nullable = false)
    private LocalDate checkIn;

    @Column(nullable = false)
    private LocalDate checkOut;

    @Column(nullable = false)
    private Integer totalPrice;

    private String status;
    private Integer numOfGuests;
    private String specialRequests;
}