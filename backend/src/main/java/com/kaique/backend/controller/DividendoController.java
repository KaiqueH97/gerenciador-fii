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
@CrossOrigin(origins = "*") 
@RequiredArgsConstructor
public class DividendoController {

    private final DividendoService service;

    @PostMapping("/ativo/{ativoId}")
    public ResponseEntity<Dividendo> adicionarDividendo(
            @PathVariable Long ativoId, 
            @RequestBody Dividendo dividendo) {
        
        Dividendo salvo = service.salvar(ativoId, dividendo);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @GetMapping("/ativo/{ativoId}")
    public ResponseEntity<List<Dividendo>> buscarPorAtivo(@PathVariable Long ativoId) {
        return ResponseEntity.ok(service.listarPorAtivo(ativoId));
    }

    @GetMapping
    public ResponseEntity<List<Dividendo>> listarMeusDividendos() {
        List<Dividendo> meusDividendos = service.listarTodos();

        return ResponseEntity.ok(meusDividendos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirDividendo(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build(); 
    }

    @PutMapping("/{id}")
    public ResponseEntity<Dividendo> atualizarDividendo(@PathVariable Long id, @RequestBody Dividendo dividendo) {
        Dividendo atualizado = service.atualizar(id, dividendo);
        return ResponseEntity.ok(atualizado);
    }
}