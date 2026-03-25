package com.kaique.backend.controller;

import com.kaique.backend.dto.AtivoResponseDTO;
import com.kaique.backend.model.Ativo;
import com.kaique.backend.model.Usuario;
import com.kaique.backend.repository.AtivoRepository; 
import com.kaique.backend.repository.UsuarioRepository; 
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
    
    private final AtivoRepository ativoRepository;
    private final UsuarioRepository usuarioRepository;

    @GetMapping
    public ResponseEntity<List<AtivoResponseDTO>> listarMeusAtivos() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String usernameLogado = auth.getName();

        List<Ativo> ativos = ativoRepository.findByUsuarioUsername(usernameLogado);

        List<AtivoResponseDTO> ativosDTO = ativos.stream()
                .map(AtivoResponseDTO::new)
                .toList();

        return ResponseEntity.ok(ativosDTO);
    }

    @PostMapping
    public ResponseEntity<AtivoResponseDTO> salvarMeuAtivo(@RequestBody Ativo ativo) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String usernameLogado = auth.getName();

        Usuario usuarioDono = usuarioRepository.findByUsername(usernameLogado)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        ativo.setUsuario(usuarioDono);
        
        Ativo novoAtivo = ativoRepository.save(ativo);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(new AtivoResponseDTO(novoAtivo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirAtivo(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ativo> atualizarAtivo(@PathVariable Long id, @RequestBody Ativo ativo) {
        Ativo ativoAtualizado = service.atualizar(id, ativo); 
        return ResponseEntity.ok(ativoAtualizado);
    }
}