package com.kaique.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kaique.backend.model.Dividendo;

@Repository
public interface DividendoRepository extends JpaRepository<Dividendo, Long> {

    List<Dividendo> findByAtivoUsuarioUsername(String username);
    
    List<Dividendo> findByAtivoId(Long ativoId);
}