package com.kaique.backend.controller;

import com.kaique.backend.model.Dividendo;
import com.kaique.backend.service.DividendoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dividendos")
@CrossOrigin(origins = "*") // Liberando a passagem para o nosso Angular!
@RequiredArgsConstructor
public class DividendoController {

    private final DividendoService service;

    // Rota para salvar um dividendo (Ex: POST /api/dividendos/ativo/1)
    @PostMapping("/ativo/{ativoId}")
    public ResponseEntity<Dividendo> adicionarDividendo(
            @PathVariable Long ativoId, 
            @RequestBody Dividendo dividendo) {
        
        Dividendo salvo = service.salvar(ativoId, dividendo);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    // Rota para buscar todos os dividendos de um ativo (Ex: GET /api/dividendos/ativo/1)
    @GetMapping("/ativo/{ativoId}")
    public ResponseEntity<List<Dividendo>> buscarPorAtivo(@PathVariable Long ativoId) {
        return ResponseEntity.ok(service.listarPorAtivo(ativoId));
    }

    @GetMapping
    public ResponseEntity<List<Dividendo>> buscarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    // Rota DELETE (Ex: DELETE /api/dividendos/1)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirDividendo(@PathVariable Long id) {
        service.excluir(id);
        // O status 204 No Content significa "Deletado com sucesso, não tenho nada para retornar"
        return ResponseEntity.noContent().build(); 
    }

    @PutMapping("/{id}")
    public ResponseEntity<Dividendo> atualizarDividendo(@PathVariable Long id, @RequestBody Dividendo dividendo) {
        Dividendo atualizado = service.atualizar(id, dividendo);
        return ResponseEntity.ok(atualizado);
    }
}