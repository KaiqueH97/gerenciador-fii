package com.kaique.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_ativos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Ativo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String ticker; 

    @Column(nullable = false)
    private String tipo; 

    private Integer quantidadeCotas;

    private Double precoMedio;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
}