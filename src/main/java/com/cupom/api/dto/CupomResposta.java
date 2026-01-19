package com.cupom.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de resposta de cupons
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CupomResposta {
    private Long id;
    private String codigo;
    private String descricao;
    private BigDecimal valorDesconto;
    private LocalDate dataExpiracao;
    private Boolean publicado;
    private Boolean excluido;
    private Boolean ativo;
    private Boolean expirado;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
    private LocalDateTime excluidoEm;
}
