package com.itu16.spring_ticketing.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itu16.spring_ticketing.models.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByUserName(String userName);
    
}
