package com.cupom.api.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

/**
 * Testes unitários para a entidade Cupom
 */
class CupomTest {

    @Test
    void deveCriarCupom() {
        Cupom cupom = new Cupom();
        cupom.setCodigo("ABC123");
        cupom.setDescricao("Cupom de teste");
        cupom.setValorDesconto(BigDecimal.valueOf(10.00));
        cupom.setDataExpiracao(LocalDate.now().plusDays(30));
        cupom.setPublicado(false);

        assertThat(cupom.getCodigo()).isEqualTo("ABC123");
        assertThat(cupom.getDescricao()).isEqualTo("Cupom de teste");
        assertThat(cupom.getValorDesconto()).isEqualByComparingTo(BigDecimal.valueOf(10.00));
        assertThat(cupom.getDataExpiracao()).isAfter(LocalDate.now());
        assertThat(cupom.getPublicado()).isFalse();
        assertThat(cupom.getExcluido()).isFalse();
    }

    @Test
    void deveNormalizarCodigo() {
        String normalizado = Cupom.normalizarCodigo("ABC123");
        assertThat(normalizado).isEqualTo("ABC123");
    }

    @Test
    void deveNormalizarCodigoComMinusculas() {
        String normalizado = Cupom.normalizarCodigo("abc123");
        assertThat(normalizado).isEqualTo("ABC123");
    }

    @Test
    void deveNormalizarCodigoComCaracteresEspeciais() {
        String normalizado = Cupom.normalizarCodigo("AB@C-12#3!");
        assertThat(normalizado).isEqualTo("ABC123");
    }

    @Test
    void deveNormalizarCodigoComEspacos() {
        String normalizado = Cupom.normalizarCodigo("AB C 12 3");
        assertThat(normalizado).isEqualTo("ABC123");
    }

    @Test
    void deveLancarExcecaoAoNormalizarCodigoVazio() {
        assertThatThrownBy(() -> Cupom.normalizarCodigo(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Código do cupom não pode ser vazio");
    }

    @Test
    void deveLancarExcecaoAoNormalizarCodigoApenasCaracteresEspeciais() {
        assertThatThrownBy(() -> Cupom.normalizarCodigo("@#$%&*"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("pelo menos 6 caracteres alfanuméricos");
    }

    @Test
    void deveLancarExcecaoAoNormalizarCodigoMuitoCurto() {
        assertThatThrownBy(() -> Cupom.normalizarCodigo("AB12"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("pelo menos 6 caracteres alfanuméricos");
    }

    @Test
    void deveLancarExcecaoAoNormalizarCodigoNulo() {
        assertThatThrownBy(() -> Cupom.normalizarCodigo(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Código do cupom não pode ser vazio");
    }

    @Test
    void deveValidarValorDesconto() {
        assertThatNoException().isThrownBy(() ->
                Cupom.validarValorDesconto(BigDecimal.valueOf(10.00)));
    }

    @Test
    void deveValidarValorDescontoMinimo() {
        assertThatNoException().isThrownBy(() ->
                Cupom.validarValorDesconto(BigDecimal.valueOf(0.5)));
    }

    @Test
    void deveLancarExcecaoComValorDescontoAbaixoMinimo() {
        assertThatThrownBy(() -> Cupom.validarValorDesconto(BigDecimal.valueOf(0.3)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Valor de desconto deve ser no mínimo 0.5");
    }

    @Test
    void deveLancarExcecaoComValorDescontoNulo() {
        assertThatThrownBy(() -> Cupom.validarValorDesconto(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("obrigatório");
    }

    @Test
    void deveLancarExcecaoComDataExpiracaoPassada() {
        assertThatThrownBy(() -> Cupom.validarDataExpiracao(LocalDate.now().minusDays(1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Data de expiração não pode ser no passado");
    }

    @Test
    void deveValidarDataExpiracaoHoje() {
        assertThatNoException().isThrownBy(() ->
                Cupom.validarDataExpiracao(LocalDate.now()));
    }

    @Test
    void deveValidarDataExpiracaoFutura() {
        assertThatNoException().isThrownBy(() ->
                Cupom.validarDataExpiracao(LocalDate.now().plusDays(30)));
    }

    @Test
    void deveLancarExcecaoComDataExpiracaoNula() {
        assertThatThrownBy(() -> Cupom.validarDataExpiracao(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("obrigatória");
    }

    @Test
    void deveVerificarSeEstaExpirado() {
        Cupom cupom = new Cupom();
        cupom.setDataExpiracao(LocalDate.now().minusDays(1));

        assertThat(cupom.estaExpirado()).isTrue();
    }

    @Test
    void deveVerificarSeNaoEstaExpirado() {
        Cupom cupom = new Cupom();
        cupom.setDataExpiracao(LocalDate.now().plusDays(1));

        assertThat(cupom.estaExpirado()).isFalse();
    }

    @Test
    void deveVerificarSeNaoEstaExpiradoHoje() {
        Cupom cupom = new Cupom();
        cupom.setDataExpiracao(LocalDate.now());

        assertThat(cupom.estaExpirado()).isFalse();
    }

    @Test
    void deveVerificarSeEstaAtivo() {
        Cupom cupom = new Cupom();
        cupom.setCodigo("ABC123");
        cupom.setDescricao("Cupom de teste");
        cupom.setValorDesconto(BigDecimal.valueOf(10.00));
        cupom.setDataExpiracao(LocalDate.now().plusDays(30));
        cupom.setPublicado(true);
        cupom.setExcluido(false);

        assertThat(cupom.estaAtivo()).isTrue();
    }

    @Test
    void deveVerificarSeNaoEstaAtivoQuandoExcluido() {
        Cupom cupom = new Cupom();
        cupom.setCodigo("ABC123");
        cupom.setDescricao("Cupom de teste");
        cupom.setValorDesconto(BigDecimal.valueOf(10.00));
        cupom.setDataExpiracao(LocalDate.now().plusDays(30));
        cupom.setPublicado(true);
        cupom.setExcluido(true);

        assertThat(cupom.estaAtivo()).isFalse();
    }

    @Test
    void deveVerificarSeNaoEstaAtivoQuandoExpirado() {
        Cupom cupom = new Cupom();
        cupom.setCodigo("ABC123");
        cupom.setDescricao("Cupom de teste");
        cupom.setValorDesconto(BigDecimal.valueOf(10.00));
        cupom.setDataExpiracao(LocalDate.now().minusDays(1));
        cupom.setPublicado(true);
        cupom.setExcluido(false);

        assertThat(cupom.estaAtivo()).isFalse();
    }

    @Test
    void deveExcluirCupomComSoftDelete() {
        Cupom cupom = new Cupom();
        cupom.setCodigo("ABC123");
        cupom.setDescricao("Cupom de teste");
        cupom.setValorDesconto(BigDecimal.valueOf(10.00));
        cupom.setDataExpiracao(LocalDate.now().plusDays(30));
        cupom.setPublicado(false);
        cupom.setExcluido(false);

        cupom.excluirSoft();

        assertThat(cupom.getExcluido()).isTrue();
        assertThat(cupom.getExcluidoEm()).isNotNull();
    }

    @Test
    void devePublicarCupom() {
        Cupom cupom = new Cupom();
        cupom.setCodigo("ABC123");
        cupom.setDescricao("Cupom de teste");
        cupom.setValorDesconto(BigDecimal.valueOf(10.00));
        cupom.setDataExpiracao(LocalDate.now().plusDays(30));
        cupom.setPublicado(false);

        cupom.publicar();

        assertThat(cupom.getPublicado()).isTrue();
    }

    @Test
    void deveDespublicarCupom() {
        Cupom cupom = new Cupom();
        cupom.setCodigo("ABC123");
        cupom.setDescricao("Cupom de teste");
        cupom.setValorDesconto(BigDecimal.valueOf(10.00));
        cupom.setDataExpiracao(LocalDate.now().plusDays(30));
        cupom.setPublicado(true);

        cupom.despublicar();

        assertThat(cupom.getPublicado()).isFalse();
    }
}
