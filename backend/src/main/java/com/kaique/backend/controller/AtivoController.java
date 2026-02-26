package com.kaique.backend.controller;

import com.kaique.backend.model.Ativo;
import com.kaique.backend.service.AtivoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ativos")
@CrossOrigin(origins = "*") // Permite que o Angular (ou qualquer outro front) acesse esta API
@RequiredArgsConstructor
public class AtivoController {

    private final AtivoService service;

    @PostMapping
    public ResponseEntity<Ativo> criarAtivo(@RequestBody Ativo ativo) {
        Ativo novoAtivo = service.salvar(ativo);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoAtivo);
    }

    @GetMapping
    public ResponseEntity<List<Ativo>> listarAtivos() {
        List<Ativo> ativos = service.listarTodos();
        return ResponseEntity.ok(ativos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirAtivo(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build(); // Retorna sucesso (204)
    }

    // Rota PUT (Ex: PUT /api/ativos/1)
    @PutMapping("/{id}")
    public ResponseEntity<Ativo> atualizarAtivo(@PathVariable Long id, @RequestBody Ativo ativo) {
        Ativo ativoAtualizado = service.atualizar(id, ativo); // Cuidado: verifique se o nome da sua variável é 'service' ou 'ativoService'
        return ResponseEntity.ok(ativoAtualizado);
    }


}