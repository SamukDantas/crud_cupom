package com.cupom.api.repository;

import com.cupom.api.domain.Cupom;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes de integração para o CupomRepositorio
 * Testa callbacks JPA (@PrePersist e @PreUpdate)
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Testes do Repositório de Cupons")
class CupomRepositorioTest {

    @Autowired
    private CupomRepositorio cupomRepositorio;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        cupomRepositorio.deleteAll();
    }

    @Test
    @DisplayName("Deve definir criadoEm e atualizadoEm ao persistir cupom")
    void deveDefinirTimestampsAoCriarCupom() {
        Cupom cupom = Cupom.builder()
                .codigo("ABC123")
                .descricao("Cupom de teste")
                .valorDesconto(new BigDecimal("10.00"))
                .dataExpiracao(LocalDate.now().plusDays(30))
                .publicado(false)
                .excluido(false)
                .build();

        Cupom cupomSalvo = cupomRepositorio.save(cupom);

        assertThat(cupomSalvo.getCriadoEm()).isNotNull();
        assertThat(cupomSalvo.getAtualizadoEm()).isNotNull();
    }

    @Test
    @DisplayName("Deve atualizar atualizadoEm ao modificar cupom")
    void deveAtualizarTimestampAoModificarCupom() throws InterruptedException {
        Cupom cupom = Cupom.builder()
                .codigo("ABC123")
                .descricao("Cupom de teste")
                .valorDesconto(new BigDecimal("10.00"))
                .dataExpiracao(LocalDate.now().plusDays(30))
                .publicado(false)
                .excluido(false)
                .build();

        Cupom cupomSalvo = cupomRepositorio.save(cupom);
        entityManager.flush();
        var atualizadoEmOriginal = cupomSalvo.getAtualizadoEm();

        // Aguarda um pouco para garantir que o timestamp será diferente
        Thread.sleep(10);

        cupomSalvo.setDescricao("Cupom atualizado");
        Cupom cupomAtualizado = cupomRepositorio.save(cupomSalvo);
        entityManager.flush();

        assertThat(cupomAtualizado.getAtualizadoEm()).isNotNull();
        assertThat(cupomAtualizado.getAtualizadoEm()).isAfter(atualizadoEmOriginal);
        assertThat(cupomAtualizado.getCriadoEm()).isEqualTo(cupomSalvo.getCriadoEm());
    }

    @Test
    @DisplayName("Deve buscar cupom por código ativo")
    void deveBuscarPorCodigoAtivo() {
        Cupom cupom = Cupom.builder()
                .codigo("ABC123")
                .descricao("Cupom de teste")
                .valorDesconto(new BigDecimal("10.00"))
                .dataExpiracao(LocalDate.now().plusDays(30))
                .publicado(false)
                .excluido(false)
                .build();

        cupomRepositorio.save(cupom);

        assertThat(cupomRepositorio.buscarPorCodigoEExcluidoFalso("ABC123")).isPresent();
    }

    @Test
    @DisplayName("Não deve buscar cupom excluído por código")
    void naoDeveBuscarCupomExcluidoPorCodigo() {
        Cupom cupom = Cupom.builder()
                .codigo("ABC123")
                .descricao("Cupom de teste")
                .valorDesconto(new BigDecimal("10.00"))
                .dataExpiracao(LocalDate.now().plusDays(30))
                .publicado(false)
                .excluido(true)
                .build();

        cupomRepositorio.save(cupom);

        assertThat(cupomRepositorio.buscarPorCodigoEExcluidoFalso("ABC123")).isEmpty();
    }

    @Test
    @DisplayName("Deve verificar existência de cupom ativo por código")
    void deveVerificarExistenciaDeCupomAtivo() {
        Cupom cupom = Cupom.builder()
                .codigo("ABC123")
                .descricao("Cupom de teste")
                .valorDesconto(new BigDecimal("10.00"))
                .dataExpiracao(LocalDate.now().plusDays(30))
                .publicado(false)
                .excluido(false)
                .build();

        cupomRepositorio.save(cupom);

        assertThat(cupomRepositorio.existePorCodigoEExcluidoFalso("ABC123")).isTrue();
    }

    @Test
    @DisplayName("Não deve encontrar cupom excluído ao verificar existência")
    void naoDeveEncontrarCupomExcluidoAoVerificarExistencia() {
        Cupom cupom = Cupom.builder()
                .codigo("ABC123")
                .descricao("Cupom de teste")
                .valorDesconto(new BigDecimal("10.00"))
                .dataExpiracao(LocalDate.now().plusDays(30))
                .publicado(false)
                .excluido(true)
                .build();

        cupomRepositorio.save(cupom);

        assertThat(cupomRepositorio.existePorCodigoEExcluidoFalso("ABC123")).isFalse();
    }

    @Test
    @DisplayName("Deve buscar todos os cupons ativos")
    void deveBuscarTodosCuponsAtivos() {
        Cupom cupom1 = Cupom.builder()
                .codigo("ABC123")
                .descricao("Cupom 1")
                .valorDesconto(new BigDecimal("10.00"))
                .dataExpiracao(LocalDate.now().plusDays(30))
                .publicado(false)
                .excluido(false)
                .build();

        Cupom cupom2 = Cupom.builder()
                .codigo("DEF456")
                .descricao("Cupom 2")
                .valorDesconto(new BigDecimal("20.00"))
                .dataExpiracao(LocalDate.now().plusDays(60))
                .publicado(false)
                .excluido(true)
                .build();

        cupomRepositorio.save(cupom1);
        cupomRepositorio.save(cupom2);

        var cuponsAtivos = cupomRepositorio.buscarTodosAtivos();

        assertThat(cuponsAtivos).hasSize(1);
        assertThat(cuponsAtivos.get(0).getCodigo()).isEqualTo("ABC123");
    }
}
