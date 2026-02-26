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

    @Column(nullable = false, unique = true)
    private String ticker; // Ex: MXRF11, ITSA4

    @Column(nullable = false)
    private String tipo; // Ex: FII, ACAO, RENDA_FIXA

    private Integer quantidadeCotas;

    private Double precoMedio;
}