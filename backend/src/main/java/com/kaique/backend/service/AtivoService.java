package com.kaique.backend.service;

import com.kaique.backend.model.Ativo;
import com.kaique.backend.model.Dividendo;
import com.kaique.backend.model.Usuario;
import com.kaique.backend.repository.AtivoRepository;
import com.kaique.backend.repository.DividendoRepository;
import com.kaique.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AtivoService {
 
    private final AtivoRepository ativoRepository;
    private final DividendoRepository dividendoRepository;
    private final UsuarioRepository usuarioRepository;
 
    private String getUsernameLogado() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
 
    public List<Ativo> listarPorUsuarioLogado() {
        return ativoRepository.findByUsuarioUsername(getUsernameLogado());
    }
 
    public Ativo salvar(Ativo ativo) {
        String username = getUsernameLogado();
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        ativo.setUsuario(usuario);
        return ativoRepository.save(ativo);
    }
 
    @Transactional
    public void excluir(Long id) {
        Ativo ativo = ativoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ativo não encontrado com o ID: " + id));
 
        if (!ativo.getUsuario().getUsername().equals(getUsernameLogado())) {
            throw new RuntimeException("Acesso negado. Você não pode excluir um ativo que não é seu.");
        }
 
        List<Dividendo> dividendos = dividendoRepository.findByAtivoId(id);
        dividendoRepository.deleteAll(dividendos);
        ativoRepository.deleteById(id);
    }
 
    public Ativo atualizar(Long id, Ativo ativoAtualizado) {
        Ativo ativoExistente = ativoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ativo não encontrado"));
 
        if (!ativoExistente.getUsuario().getUsername().equals(getUsernameLogado())) {
            throw new RuntimeException("Acesso negado. Você não pode atualizar um ativo que não é seu.");
        }
 
        ativoExistente.setTicker(ativoAtualizado.getTicker());
        ativoExistente.setTipo(ativoAtualizado.getTipo());
        ativoExistente.setQuantidadeCotas(ativoAtualizado.getQuantidadeCotas());
        ativoExistente.setPrecoMedio(ativoAtualizado.getPrecoMedio());
        
        return ativoRepository.save(ativoExistente);
    }
}