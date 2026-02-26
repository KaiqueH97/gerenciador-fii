package com.kaique.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "tb_dividendos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Dividendo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double valor; // O valor que você recebeu, ex: 49.00

    @Column(nullable = false)
    private LocalDate dataPagamento; // A data exata ou o mês que caiu na conta

    // Aqui está a mágica do banco relacional! 
    // Isso diz que vários dividendos pertencem a um único Ativo.
    @ManyToOne
    @JoinColumn(name = "ativo_id", nullable = false)
    private Ativo ativo;
}