package com.smartcampus.theatre.model;

import jakarta.persistence.*;

/**
 * One reserved seat for one show. The unique constraint on (show_id, seat_label)
 * guarantees at the database level that a seat can never be double-booked,
 * even when two users try to book it at the same moment.
 * Rows are deleted when a booking is cancelled, which frees the seat again.
 */
@Entity
@Table(name = "booked_seat",
        uniqueConstraints = @UniqueConstraint(name = "uk_show_seat", columnNames = {"show_id", "seat_label"}))
public class BookedSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "show_id")
    private Show show;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Booking booking;

    @Column(name = "seat_label", nullable = false)
    private String seatLabel;

    public BookedSeat() {}

    public BookedSeat(Show show, Booking booking, String seatLabel) {
        this.show = show;
        this.booking = booking;
        this.seatLabel = seatLabel;
    }

    public Long getId() { return id; }
    public Show getShow() { return show; }
    public Booking getBooking() { return booking; }
    public String getSeatLabel() { return seatLabel; }
}
