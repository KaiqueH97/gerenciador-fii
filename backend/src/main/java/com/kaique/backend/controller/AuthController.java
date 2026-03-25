package com.kaique.backend.controller;

import com.kaique.backend.model.Usuario;
import com.kaique.backend.repository.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registrarUsuario(@RequestBody Usuario usuario) {
        if (usuarioRepository.findByUsername(usuario.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Erro: Este usuário já existe!");
        }

        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        
        usuarioRepository.save(usuario);
        
        return ResponseEntity.ok("Usuário criado com sucesso!");
    }
}