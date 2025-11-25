package com.ticketnotify.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ticketnotify.entity.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {

}
