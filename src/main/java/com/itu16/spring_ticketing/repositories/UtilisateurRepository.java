package com.itu16.spring_ticketing.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itu16.spring_ticketing.models.Utilisateur;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByUserName(String userName);
}