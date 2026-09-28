package com.example.hormiga_charlie.repository;

import com.example.hormiga_charlie.model.ConfiguracionJuego;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfiguracionJuegoRepository extends JpaRepository<ConfiguracionJuego, Integer> {
}
