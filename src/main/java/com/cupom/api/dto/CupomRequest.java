package com.cupom.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para criação/atualização de cupons
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CupomRequest {

    @NotBlank(message = "Código é obrigatório")
    @Size(min = 6, message = "Código deve ter no mínimo 6 caracteres alfanuméricos")
    private String code;

    @NotBlank(message = "Descrição é obrigatória")
    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    private String description;

    @NotNull(message = "Valor de desconto é obrigatório")
    @DecimalMin(value = "0.5", message = "Valor de desconto deve ser no mínimo 0.5")
    private BigDecimal discountValue;

    @NotNull(message = "Data de expiração é obrigatória")
    private LocalDate expirationDate;

    private Boolean published;
}
