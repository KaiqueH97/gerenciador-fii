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
public class DividendoService {

    private final DividendoRepository dividendoRepository;
    private final AtivoRepository ativoRepository; // Precisamos acessar o banco de ativos também!

    public Dividendo salvar(Long ativoId, Dividendo dividendo) {
        // Busca o ativo no banco. Se não achar, lança um erro.
        Ativo ativo = ativoRepository.findById(ativoId)
                .orElseThrow(() -> new RuntimeException("Ativo não encontrado com o ID: " + ativoId));
        
        // Associa o ativo encontrado ao dividendo
        dividendo.setAtivo(ativo);
        
        // Salva o dividendo no banco de dados
        return dividendoRepository.save(dividendo);
    }

    public List<Dividendo> listarPorAtivo(Long ativoId) {
        return dividendoRepository.findByAtivoId(ativoId);
    }

    public List<Dividendo> listarTodos() {
        return dividendoRepository.findAll();
    }

    public void excluir(Long id) {
        dividendoRepository.deleteById(id);
    }

    public Dividendo atualizar(Long id, Dividendo dividendoAtualizado) {
        Dividendo existente = dividendoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dividendo não encontrado"));
        
        existente.setValor(dividendoAtualizado.getValor());
        existente.setDataPagamento(dividendoAtualizado.getDataPagamento());
        
        return dividendoRepository.save(existente);
    }
}