package com.cupom.api.entity;

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
 * Encapsula todas as regras de negócio relacionadas a cupons.
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
    private String code;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal discountValue;

    @Column(nullable = false)
    private LocalDate expirationDate;

    @Column(nullable = false)
    @Builder.Default
    private Boolean published = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean deleted = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Normaliza o código do cupom removendo caracteres especiais.
     * REGRA DE NEGÓCIO: Remove caracteres especiais e garante 6 caracteres.
     * 
     * @param rawCode código bruto fornecido
     * @return código normalizado com 6 caracteres alfanuméricos
     */
    public static String normalizeCode(String rawCode) {
        if (rawCode == null || rawCode.isEmpty()) {
            throw new IllegalArgumentException("Código do cupom não pode ser vazio");
        }
        
        // Remove caracteres especiais, mantendo apenas alfanuméricos
        String normalized = rawCode.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
        
        if (normalized.length() < 6) {
            throw new IllegalArgumentException(
                "Código deve ter pelo menos 6 caracteres alfanuméricos após remover caracteres especiais. "
                + "Código fornecido resulta em apenas " + normalized.length() + " caracteres."
            );
        }
        
        // Garante exatamente 6 caracteres
        return normalized.substring(0, 6);
    }

    /**
     * Valida se a data de expiração está no futuro.
     * REGRA DE NEGÓCIO: Data de expiração não pode ser no passado.
     * 
     * @param expirationDate data de expiração
     * @throws IllegalArgumentException se a data for no passado
     */
    public static void validateExpirationDate(LocalDate expirationDate) {
        if (expirationDate == null) {
            throw new IllegalArgumentException("Data de expiração é obrigatória");
        }
        
        if (expirationDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                "Data de expiração não pode ser no passado. Data fornecida: " + expirationDate
            );
        }
    }

    /**
     * Valida se o valor de desconto é válido.
     * REGRA DE NEGÓCIO: Valor mínimo é 0.5, sem máximo.
     * 
     * @param discountValue valor do desconto
     * @throws IllegalArgumentException se o valor for inválido
     */
    public static void validateDiscountValue(BigDecimal discountValue) {
        if (discountValue == null) {
            throw new IllegalArgumentException("Valor de desconto é obrigatório");
        }
        
        BigDecimal minValue = new BigDecimal("0.5");
        if (discountValue.compareTo(minValue) < 0) {
            throw new IllegalArgumentException(
                "Valor de desconto deve ser no mínimo 0.5. Valor fornecido: " + discountValue
            );
        }
    }

    /**
     * Verifica se o cupom está expirado.
     * 
     * @return true se expirado
     */
    public boolean isExpired() {
        return LocalDate.now().isAfter(this.expirationDate);
    }

    /**
     * Marca o cupom como deletado (soft delete).
     * REGRA DE NEGÓCIO: Soft delete - não perde informações.
     * 
     * @throws IllegalStateException se o cupom já estiver deletado
     */
    public void softDelete() {
        if (this.deleted) {
            throw new IllegalStateException("Cupom já está deletado");
        }
        
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    /**
     * Verifica se o cupom está ativo (não deletado e não expirado).
     * 
     * @return true se ativo
     */
    public boolean isActive() {
        return !this.deleted && !isExpired();
    }

    /**
     * Publica o cupom.
     */
    public void publish() {
        this.published = true;
    }

    /**
     * Despublica o cupom.
     */
    public void unpublish() {
        this.published = false;
    }
}
