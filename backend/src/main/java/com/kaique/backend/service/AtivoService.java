package com.kaique.backend.service;

import com.kaique.backend.model.Ativo;
import com.kaique.backend.model.Dividendo;
import com.kaique.backend.repository.AtivoRepository;
import com.kaique.backend.repository.DividendoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AtivoService {

    private final AtivoRepository ativoRepository;
    private final DividendoRepository dividendoRepository; 

    public List<Ativo> listarTodos() {
        return ativoRepository.findAll();
    }

    public Ativo salvar(Ativo ativo) {
        return ativoRepository.save(ativo);
    }

    public void excluir(Long id) {
        List<Dividendo> dividendos = dividendoRepository.findByAtivoId(id);
        
        dividendoRepository.deleteAll(dividendos);
        
        ativoRepository.deleteById(id);
    }

    public Ativo atualizar(Long id, Ativo ativoAtualizado) {
        Ativo ativoExistente = ativoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ativo não encontrado"));
        
        ativoExistente.setTicker(ativoAtualizado.getTicker());
        ativoExistente.setTipo(ativoAtualizado.getTipo());
        ativoExistente.setQuantidadeCotas(ativoAtualizado.getQuantidadeCotas());
        ativoExistente.setPrecoMedio(ativoAtualizado.getPrecoMedio());
        
        return ativoRepository.save(ativoExistente);
    }
}