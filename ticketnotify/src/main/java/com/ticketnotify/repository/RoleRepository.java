package com.ticketnotify.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ticketnotify.entity.Role;

public interface RoleRepository extends JpaRepository<Role, UUID> {

}
