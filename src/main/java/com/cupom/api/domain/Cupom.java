package com.cupom.api.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidade de domínio que representa um Cupom de desconto.
 */
@Entity
@Table(name = "cupons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cupom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 6)
    private String codigo;

    @Column(nullable = false, length = 500)
    private String descricao;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorDesconto;

    @Column(nullable = false)
    private LocalDate dataExpiracao;

    @Column(nullable = false)
    @Builder.Default
    private Boolean publicado = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean excluido = false;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @Column(name = "excluido_em")
    private LocalDateTime excluidoEm;

    @PrePersist
    protected void aoCriar() {
        criadoEm = LocalDateTime.now();
        atualizadoEm = LocalDateTime.now();
    }

    @PreUpdate
    protected void aoAtualizar() {
        atualizadoEm = LocalDateTime.now();
    }

    /**
     * Normaliza o código do cupom removendo caracteres especiais.
     * 
     * @param codigoBruto código bruto fornecido
     * @return código normalizado com 6 caracteres alfanuméricos
     */
    public static String normalizarCodigo(String codigoBruto) {
        if (codigoBruto == null || codigoBruto.isEmpty()) {
            throw new IllegalArgumentException("Código do cupom não pode ser vazio");
        }
        
        String normalizado = codigoBruto.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
        
        if (normalizado.length() < 6) {
            throw new IllegalArgumentException(
                "Código deve ter pelo menos 6 caracteres alfanuméricos após remover caracteres especiais. "
                + "Código fornecido resulta em apenas " + normalizado.length() + " caracteres."
            );
        }
        
        return normalizado.substring(0, 6);
    }

    /**
     * Valida se a data de expiração está no futuro.
     * 
     * @param dataExpiracao data de expiração
     * @throws IllegalArgumentException se a data for no passado
     */
    public static void validarDataExpiracao(LocalDate dataExpiracao) {
        if (dataExpiracao == null) {
            throw new IllegalArgumentException("Data de expiração é obrigatória");
        }
        
        if (dataExpiracao.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                "Data de expiração não pode ser no passado. Data fornecida: " + dataExpiracao
            );
        }
    }

    /**
     * Valida se o valor de desconto é válido.
     * 
     * @param valorDesconto valor do desconto
     * @throws IllegalArgumentException se o valor for inválido
     */
    public static void validarValorDesconto(BigDecimal valorDesconto) {
        if (valorDesconto == null) {
            throw new IllegalArgumentException("Valor de desconto é obrigatório");
        }
        
        BigDecimal valorMinimo = new BigDecimal("0.5");
        if (valorDesconto.compareTo(valorMinimo) < 0) {
            throw new IllegalArgumentException(
                "Valor de desconto deve ser no mínimo 0.5. Valor fornecido: " + valorDesconto
            );
        }
    }

    public boolean estaExpirado() {
        return LocalDate.now().isAfter(this.dataExpiracao);
    }

    /**
     * Marca o cupom como deletado (soft delete).
     * 
     * @throws IllegalStateException se o cupom já estiver deletado
     */
    public void excluirSoft() {
        if (this.excluido) {
            throw new IllegalStateException("Cupom já está deletado");
        }
        
        this.excluido = true;
        this.excluidoEm = LocalDateTime.now();
    }

    public boolean estaAtivo() {
        return !this.excluido && !estaExpirado();
    }

    public void publicar() {
        this.publicado = true;
    }

    public void despublicar() {
        this.publicado = false;
    }
}
