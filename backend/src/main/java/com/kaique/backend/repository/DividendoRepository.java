package com.kaique.backend.repository;

import com.kaique.backend.model.Dividendo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DividendoRepository extends JpaRepository<Dividendo, Long> {
    
    // O Spring cria a busca no banco de dados automaticamente só por causa deste nome!
    List<Dividendo> findByAtivoId(Long ativoId);
}