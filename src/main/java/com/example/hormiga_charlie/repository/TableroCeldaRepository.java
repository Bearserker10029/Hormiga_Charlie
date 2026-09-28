package com.example.hormiga_charlie.repository;

import com.example.hormiga_charlie.model.TableroCelda;
import com.example.hormiga_charlie.model.TableroCeldaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TableroCeldaRepository extends JpaRepository<TableroCelda, TableroCeldaId> {

}
