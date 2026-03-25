package com.kaique.backend.controller;

import com.kaique.backend.dto.AtivoResponseDTO;
import com.kaique.backend.model.Ativo;
import com.kaique.backend.service.AtivoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ativos")
@CrossOrigin(origins = "*") 
@RequiredArgsConstructor
public class AtivoController {

    private final AtivoService service;

    @GetMapping
    public ResponseEntity<List<AtivoResponseDTO>> listarMeusAtivos() {
        List<Ativo> ativos = service.listarPorUsuarioLogado();
        List<AtivoResponseDTO> ativosDTO = ativos.stream()
                .map(AtivoResponseDTO::new)
                .toList();
        return ResponseEntity.ok(ativosDTO);
    }

    @PostMapping
    public ResponseEntity<AtivoResponseDTO> salvarMeuAtivo(@RequestBody Ativo ativo) {
        Ativo novoAtivo = service.salvar(ativo);
        return ResponseEntity.status(HttpStatus.CREATED).body(new AtivoResponseDTO(novoAtivo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirAtivo(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<AtivoResponseDTO> atualizarAtivo(@PathVariable Long id, @RequestBody Ativo ativo) {
        Ativo ativoAtualizado = service.atualizar(id, ativo);
        return ResponseEntity.ok(new AtivoResponseDTO(ativoAtualizado));
    }
}