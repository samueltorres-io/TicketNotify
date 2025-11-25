package com.ticketnotify.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ticketnotify.entity.Event;

public interface EventRepository extends JpaRepository<Event, UUID> {

}
