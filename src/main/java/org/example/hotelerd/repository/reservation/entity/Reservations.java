package org.example.hotelerd.repository.reservation.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.example.hotelerd.repository.hotel.entity.Room;
import org.example.hotelerd.repository.hotel.entity.RoomDatePrice;
import org.example.hotelerd.repository.hotel.entity.RoomType;
import org.example.hotelerd.repository.user.entity.Users;

@Entity
@Getter
@Builder
@AllArgsConstructor
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservations {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_date_price_id", nullable = false)
    private RoomDatePrice roomDatePrice;


    @Column(nullable = false)
    private Integer totalPrice;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    private Integer numOfGuests;
    private String specialRequests;

    public void cancel() {
        this.status = ReservationStatus.CANCELLED;
    }

    public void validateCancelPermission(Integer userId) {
        if (!this.user.getId().equals(userId)) {
            throw new IllegalArgumentException("해당 예약을 취소할 권한이 없습니다.");
        }
    }

    public void validateCancellable() {
        if (this.status == ReservationStatus.CANCELLED) {
            throw new IllegalStateException("이미 취소된 예약입니다.");
        }

        LocalDate today = LocalDate.now();
        LocalDate reservationDate = this.roomDatePrice.getDateAvailable();

        if (reservationDate.isBefore(today) || reservationDate.isEqual(today)) {
            throw new IllegalStateException("당일 또는 지난 예약은 취소할 수 없습니다.");
        }
    }
}