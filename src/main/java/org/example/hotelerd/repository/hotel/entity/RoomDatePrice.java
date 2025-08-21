package org.example.hotelerd.repository.hotel.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@ToString
@AllArgsConstructor
@Table(name = "room_date_price",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_roomtype_date", columnNames = {"room_type_id",
            "date_available"})
    }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoomDatePrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "room_type_id", nullable = false)
    private RoomType roomType;

    private LocalDate dateAvailable;
    private Integer price;
    private Integer quantity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "season_id")
    private Season season;
}
