package com.smartcampus.theatre.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "shows")
public class Show {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    private String genre;

    @Column(nullable = false)
    private int durationMinutes;

    @Column(nullable = false)
    private LocalDateTime showTime;

    @Column(nullable = false)
    private String hall;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal ticketPrice;

    /** Seat layout: rows are letters A, B, C...; seats per row are numbered 1..N. */
    @Column(nullable = false)
    private int totalRows;

    @Column(nullable = false)
    private int seatsPerRow;

    public int getCapacity() {
        return totalRows * seatsPerRow;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
    public LocalDateTime getShowTime() { return showTime; }
    public void setShowTime(LocalDateTime showTime) { this.showTime = showTime; }
    public String getHall() { return hall; }
    public void setHall(String hall) { this.hall = hall; }
    public BigDecimal getTicketPrice() { return ticketPrice; }
    public void setTicketPrice(BigDecimal ticketPrice) { this.ticketPrice = ticketPrice; }
    public int getTotalRows() { return totalRows; }
    public void setTotalRows(int totalRows) { this.totalRows = totalRows; }
    public int getSeatsPerRow() { return seatsPerRow; }
    public void setSeatsPerRow(int seatsPerRow) { this.seatsPerRow = seatsPerRow; }
}
