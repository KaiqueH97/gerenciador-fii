package com.kaique.backend.service;

import com.kaique.backend.model.Ativo;
import com.kaique.backend.model.Dividendo;
import com.kaique.backend.repository.AtivoRepository;
import com.kaique.backend.repository.DividendoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DividendoService {

    private final DividendoRepository dividendoRepository;
    private final AtivoRepository ativoRepository;

    private String getUsernameLogado() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public Dividendo salvar(Long ativoId, Dividendo dividendo) {
        Ativo ativo = ativoRepository.findById(ativoId)
                .orElseThrow(() -> new RuntimeException("Ativo não encontrado com o ID: " + ativoId));

        if (!ativo.getUsuario().getUsername().equals(getUsernameLogado())) {
            throw new RuntimeException("Acesso negado. Você não pode adicionar um dividendo a um ativo que não é seu.");
        }

        dividendo.setAtivo(ativo);
        return dividendoRepository.save(dividendo);
    }

    public List<Dividendo> listarPorAtivo(Long ativoId) {
        Ativo ativo = ativoRepository.findById(ativoId)
                .orElseThrow(() -> new RuntimeException("Ativo não encontrado com o ID: " + ativoId));

        if (!ativo.getUsuario().getUsername().equals(getUsernameLogado())) {
            throw new RuntimeException("Acesso negado. O ativo não pertence ao usuário logado.");
        }
        return dividendoRepository.findByAtivoId(ativoId);
    }

    public List<Dividendo> listarTodos() {
        return dividendoRepository.findByAtivoUsuarioUsername(getUsernameLogado());
    }

    public void excluir(Long id) {
        Dividendo dividendo = dividendoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dividendo não encontrado"));

        if (!dividendo.getAtivo().getUsuario().getUsername().equals(getUsernameLogado())) {
            throw new RuntimeException("Acesso negado. Você não pode excluir um dividendo de um ativo que não é seu.");
        }

        dividendoRepository.deleteById(id);
    }

    public Dividendo atualizar(Long id, Dividendo dividendoAtualizado) {
        Dividendo existente = dividendoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dividendo não encontrado"));

        if (!existente.getAtivo().getUsuario().getUsername().equals(getUsernameLogado())) {
            throw new RuntimeException("Acesso negado. Você não pode atualizar um dividendo de um ativo que não é seu.");
        }

        existente.setValor(dividendoAtualizado.getValor());
        existente.setDataPagamento(dividendoAtualizado.getDataPagamento());

        return dividendoRepository.save(existente);
    }
}