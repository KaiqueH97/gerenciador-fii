package com.kaique.backend.dto;

import com.kaique.backend.model.Ativo;

public record AtivoResponseDTO(
        Long id,
        String ticker,
        String tipo,
        Integer quantidadeCotas,
        Double precoMedio
) {
    
    public AtivoResponseDTO(Ativo ativo) {
        this(ativo.getId(), ativo.getTicker(), ativo.getTipo(), ativo.getQuantidadeCotas(), ativo.getPrecoMedio());
    }
}