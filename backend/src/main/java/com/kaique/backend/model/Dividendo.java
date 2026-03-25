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
    private Double valor; 

    @Column(nullable = false)
    private LocalDate dataPagamento; 

    @ManyToOne
    @JoinColumn(name = "ativo_id", nullable = false)
    private Ativo ativo;
}