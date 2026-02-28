package com.kaique.backend.repository;

import com.kaique.backend.model.Ativo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AtivoRepository extends JpaRepository<Ativo, Long> {
    List<Ativo> findByUsuarioUsername(String username);    
}