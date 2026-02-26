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
    private final DividendoRepository dividendoRepository; // Adicionamos isso!

    public List<Ativo> listarTodos() {
        return ativoRepository.findAll();
    }

    public Ativo salvar(Ativo ativo) {
        return ativoRepository.save(ativo);
    }

    // A MÁGICA DA EXCLUSÃO SEGURA
    public void excluir(Long id) {
        // 1. Busca todos os dividendos atrelados a este FII/Ação
        List<Dividendo> dividendos = dividendoRepository.findByAtivoId(id);
        
        // 2. Apaga os dividendos primeiro
        dividendoRepository.deleteAll(dividendos);
        
        // 3. Agora sim, apaga o Ativo com segurança
        ativoRepository.deleteById(id);
    }

    // Método para Atualizar (Editar) um Ativo existente
    public Ativo atualizar(Long id, Ativo ativoAtualizado) {
        // 1. Procura o ativo no banco
        Ativo ativoExistente = ativoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ativo não encontrado"));
        
        // 2. Atualiza os dados com o que veio da tela
        ativoExistente.setTicker(ativoAtualizado.getTicker());
        ativoExistente.setTipo(ativoAtualizado.getTipo());
        ativoExistente.setQuantidadeCotas(ativoAtualizado.getQuantidadeCotas());
        ativoExistente.setPrecoMedio(ativoAtualizado.getPrecoMedio());
        
        // 3. Salva de volta no banco
        return ativoRepository.save(ativoExistente);
    }
}