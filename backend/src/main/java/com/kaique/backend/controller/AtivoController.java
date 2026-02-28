package com.kaique.backend.controller;

import com.kaique.backend.model.Ativo;
import com.kaique.backend.model.Usuario;
import com.kaique.backend.repository.AtivoRepository; // <-- Faltava importar
import com.kaique.backend.repository.UsuarioRepository; // <-- Faltava importar
import com.kaique.backend.service.AtivoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ativos")
@CrossOrigin(origins = "*") 
@RequiredArgsConstructor
public class AtivoController {

    private final AtivoService service;
    
    // Declarando os repositórios para o Java saber quem eles são
    private final AtivoRepository ativoRepository;
    private final UsuarioRepository usuarioRepository;

    // 1. GET (Listar apenas os MEUS ativos) - Substituiu o antigo
    @GetMapping
    public ResponseEntity<List<Ativo>> listarMeusAtivos() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String usernameLogado = auth.getName();

        List<Ativo> ativos = ativoRepository.findByUsuarioUsername(usernameLogado);
        return ResponseEntity.ok(ativos);
    }

    // 2. POST (Salvar o ativo com o MEU crachá) - Substituiu o antigo
    @PostMapping
    public ResponseEntity<Ativo> salvarMeuAtivo(@RequestBody Ativo ativo) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String usernameLogado = auth.getName();

        Usuario usuarioDono = usuarioRepository.findByUsername(usernameLogado)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        ativo.setUsuario(usuarioDono);
        
        Ativo novoAtivo = ativoRepository.save(ativo);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoAtivo);
    }

    // 3. DELETE (Mantido igual)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirAtivo(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }

    // 4. PUT (Mantido igual)
    @PutMapping("/{id}")
    public ResponseEntity<Ativo> atualizarAtivo(@PathVariable Long id, @RequestBody Ativo ativo) {
        Ativo ativoAtualizado = service.atualizar(id, ativo); 
        return ResponseEntity.ok(ativoAtualizado);
    }
}