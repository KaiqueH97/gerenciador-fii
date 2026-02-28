package com.kaique.backend.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "tb_usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    // Relacionamento: Um usuário pode ter VÁRIOS ativos
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Ativo> ativos;

    public Usuario() {}

    public Usuario(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public List<Ativo> getAtivos() { return ativos; }
    public void setAtivos(List<Ativo> ativos) { this.ativos = ativos; }
}