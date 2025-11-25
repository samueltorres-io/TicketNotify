package com.ticketnotify.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ticketnotify.entity.User;

public interface UserRepository extends JpaRepository<User, UUID> {

}
