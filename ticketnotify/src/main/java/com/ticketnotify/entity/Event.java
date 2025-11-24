package com.ticketnotify.entity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(length = 100, nullable = false)
    private String title;

    private String description;

    @Column(name = "date_event", nullable = false)
    private OffsetDateTime dateEvent;

    @Column(name = "banner_url", length = 255)
    private String bannerUrl;

    @Column(nullable = false)
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User userOwner;

    @PastOrPresent
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @PastOrPresent
    @Column(name = "updated_at", updatable = true)
    private OffsetDateTime updatedAt;
}