package com.kaique.backend.controller;

import com.kaique.backend.model.Ativo;
import com.kaique.backend.model.Dividendo;
import com.kaique.backend.repository.AtivoRepository;
import com.kaique.backend.repository.DividendoRepository;
import com.kaique.backend.service.DividendoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dividendos")
@CrossOrigin(origins = "*") 
@RequiredArgsConstructor
public class DividendoController {

    private final DividendoService service;
    private final AtivoRepository ativoRepository;
    private final DividendoRepository dividendoRepository;

    @PostMapping("/ativo/{ativoId}")
    public ResponseEntity<Dividendo> salvarDividendo(@PathVariable Long ativoId, @RequestBody Dividendo dividendo) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String usernameLogado = auth.getName();

        Ativo ativo = ativoRepository.findById(ativoId)
                .orElseThrow(() -> new RuntimeException("Ativo não encontrado"));

        if (!ativo.getUsuario().getUsername().equals(usernameLogado)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        dividendo.setAtivo(ativo);

        Dividendo novoDividendo = dividendoRepository.save(dividendo);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoDividendo);
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