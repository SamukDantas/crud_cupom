package com.cupom.api.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

/**
 * Testes unitários para a entidade Cupom.
 */
class CupomEntityTest {

    @Test
    void testCupomCreation() {
        Cupom cupom = new Cupom();
        cupom.setCode("ABC123");
        cupom.setDescription("Cupom de teste");
        cupom.setDiscountValue(BigDecimal.valueOf(10.00));
        cupom.setExpirationDate(LocalDate.now().plusDays(30));
        cupom.setPublished(false);

        assertThat(cupom.getCode()).isEqualTo("ABC123");
        assertThat(cupom.getDescription()).isEqualTo("Cupom de teste");
        assertThat(cupom.getDiscountValue()).isEqualByComparingTo(BigDecimal.valueOf(10.00));
        assertThat(cupom.getExpirationDate()).isAfter(LocalDate.now());
        assertThat(cupom.getPublished()).isFalse();
        assertThat(cupom.getDeleted()).isFalse();
    }

    @Test
    void testNormalizeCode() {
        String normalized = Cupom.normalizeCode("ABC123");
        assertThat(normalized).isEqualTo("ABC123");
    }

    @Test
    void testNormalizeCodeWithLowerCase() {
        String normalized = Cupom.normalizeCode("abc123");
        assertThat(normalized).isEqualTo("ABC123");
    }

    @Test
    void testNormalizeCodeWithSpecialChars() {
        String normalized = Cupom.normalizeCode("AB@C-12#3!");
        assertThat(normalized).isEqualTo("ABC123");
    }

    @Test
    void testNormalizeCodeWithSpaces() {
        String normalized = Cupom.normalizeCode("AB C 12 3");
        assertThat(normalized).isEqualTo("ABC123");
    }

    @Test
    void testNormalizeEmptyCode() {
        assertThatThrownBy(() -> Cupom.normalizeCode(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Código do cupom não pode ser vazio");
    }

    @Test
    void testNormalizeCodeOnlySpecialChars() {
        assertThatThrownBy(() -> Cupom.normalizeCode("@#$%&*"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("pelo menos 6 caracteres alfanuméricos");
    }

    @Test
    void testNormalizeCodeTooShort() {
        assertThatThrownBy(() -> Cupom.normalizeCode("AB12"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("pelo menos 6 caracteres alfanuméricos");
    }

    @Test
    void testNormalizeCodeNull() {
        assertThatThrownBy(() -> Cupom.normalizeCode(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Código do cupom não pode ser vazio");
    }

    @Test
    void testValidateDiscountValue() {
        assertThatNoException().isThrownBy(() ->
                Cupom.validateDiscountValue(BigDecimal.valueOf(10.00)));
    }

    @Test
    void testValidateDiscountValueMin() {
        assertThatNoException().isThrownBy(() ->
                Cupom.validateDiscountValue(BigDecimal.valueOf(0.5)));
    }

    @Test
    void testValidateDiscountValueBelowMin() {
        assertThatThrownBy(() -> Cupom.validateDiscountValue(BigDecimal.valueOf(0.3)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Valor de desconto deve ser no mínimo 0.5");
    }

    @Test
    void testValidateDiscountValueNull() {
        assertThatThrownBy(() -> Cupom.validateDiscountValue(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("obrigatório");
    }

    @Test
    void testValidateExpirationDatePast() {
        assertThatThrownBy(() -> Cupom.validateExpirationDate(LocalDate.now().minusDays(1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Data de expiração não pode ser no passado");
    }

    @Test
    void testValidateExpirationDateToday() {
        assertThatNoException().isThrownBy(() ->
                Cupom.validateExpirationDate(LocalDate.now()));
    }

    @Test
    void testValidateExpirationDateFuture() {
        assertThatNoException().isThrownBy(() ->
                Cupom.validateExpirationDate(LocalDate.now().plusDays(30)));
    }

    @Test
    void testValidateExpirationDateNull() {
        assertThatThrownBy(() -> Cupom.validateExpirationDate(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("obrigatória");
    }

    @Test
    void testIsExpiredTrue() {
        Cupom cupom = new Cupom();
        cupom.setExpirationDate(LocalDate.now().minusDays(1));

        assertThat(cupom.isExpired()).isTrue();
    }

    @Test
    void testIsExpiredFalse() {
        Cupom cupom = new Cupom();
        cupom.setExpirationDate(LocalDate.now().plusDays(1));

        assertThat(cupom.isExpired()).isFalse();
    }

    @Test
    void testIsExpiredToday() {
        Cupom cupom = new Cupom();
        cupom.setExpirationDate(LocalDate.now());

        assertThat(cupom.isExpired()).isFalse();
    }

    @Test
    void testIsActiveTrue() {
        Cupom cupom = new Cupom();
        cupom.setCode("ABC123");
        cupom.setDescription("Cupom de teste");
        cupom.setDiscountValue(BigDecimal.valueOf(10.00));
        cupom.setExpirationDate(LocalDate.now().plusDays(30));
        cupom.setPublished(true);
        cupom.setDeleted(false);

        assertThat(cupom.isActive()).isTrue();
    }

    @Test
    void testIsActiveFalseDeleted() {
        Cupom cupom = new Cupom();
        cupom.setCode("ABC123");
        cupom.setDescription("Cupom de teste");
        cupom.setDiscountValue(BigDecimal.valueOf(10.00));
        cupom.setExpirationDate(LocalDate.now().plusDays(30));
        cupom.setPublished(true);
        cupom.setDeleted(true);

        assertThat(cupom.isActive()).isFalse();
    }

    @Test
    void testIsActiveFalseExpired() {
        Cupom cupom = new Cupom();
        cupom.setCode("ABC123");
        cupom.setDescription("Cupom de teste");
        cupom.setDiscountValue(BigDecimal.valueOf(10.00));
        cupom.setExpirationDate(LocalDate.now().minusDays(1));
        cupom.setPublished(true);
        cupom.setDeleted(false);

        assertThat(cupom.isActive()).isFalse();
    }

    @Test
    void testSoftDelete() {
        Cupom cupom = new Cupom();
        cupom.setCode("ABC123");
        cupom.setDescription("Cupom de teste");
        cupom.setDiscountValue(BigDecimal.valueOf(10.00));
        cupom.setExpirationDate(LocalDate.now().plusDays(30));
        cupom.setPublished(false);
        cupom.setDeleted(false);

        cupom.softDelete();

        assertThat(cupom.getDeleted()).isTrue();
        assertThat(cupom.getDeletedAt()).isNotNull();
    }

    @Test
    void testPublish() {
        Cupom cupom = new Cupom();
        cupom.setCode("ABC123");
        cupom.setDescription("Cupom de teste");
        cupom.setDiscountValue(BigDecimal.valueOf(10.00));
        cupom.setExpirationDate(LocalDate.now().plusDays(30));
        cupom.setPublished(false);

        cupom.publish();

        assertThat(cupom.getPublished()).isTrue();
    }

    @Test
    void testUnpublish() {
        Cupom cupom = new Cupom();
        cupom.setCode("ABC123");
        cupom.setDescription("Cupom de teste");
        cupom.setDiscountValue(BigDecimal.valueOf(10.00));
        cupom.setExpirationDate(LocalDate.now().plusDays(30));
        cupom.setPublished(true);

        cupom.unpublish();

        assertThat(cupom.getPublished()).isFalse();
    }
}