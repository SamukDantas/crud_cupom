package com.cupom.api.service;

import com.cupom.api.domain.Cupom;
import com.cupom.api.dto.CupomRequisicao;
import com.cupom.api.dto.CupomResposta;
import com.cupom.api.exception.*;
import com.cupom.api.repository.CupomRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do Serviço de Cupons")
class CupomServicoTest {

    @Mock
    private CupomRepositorio cupomRepositorio;

    @InjectMocks
    private CupomServico cupomServico;

    private Cupom cupomExemplo;
    private CupomRequisicao requisicaoExemplo;

    @BeforeEach
    void setUp() {
        cupomExemplo = Cupom.builder()
                .id(1L)
                .codigo("ABC123")
                .descricao("Desconto de 10%")
                .valorDesconto(new BigDecimal("10.00"))
                .dataExpiracao(LocalDate.now().plusDays(30))
                .publicado(false)
                .excluido(false)
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .build();

        requisicaoExemplo = CupomRequisicao.builder()
                .codigo("ABC-123")
                .descricao("Desconto de 10%")
                .valorDesconto(new BigDecimal("10.00"))
                .dataExpiracao(LocalDate.now().plusDays(30))
                .publicado(false)
                .build();
    }

    @Test
    @DisplayName("Deve criar cupom com sucesso")
    void deveCriarCupomComSucesso() {
        when(cupomRepositorio.existePorCodigoEExcluidoFalso(anyString())).thenReturn(false);
        when(cupomRepositorio.save(any(Cupom.class))).thenReturn(cupomExemplo);

        CupomResposta resposta = cupomServico.criarCupom(requisicaoExemplo);

        assertThat(resposta).isNotNull();
        assertThat(resposta.getCodigo()).isEqualTo("ABC123");
        verify(cupomRepositorio, times(1)).save(any(Cupom.class));
    }

    @Test
    @DisplayName("Deve normalizar código removendo caracteres especiais")
    void deveNormalizarCodigo() {
        CupomRequisicao requisicao = CupomRequisicao.builder()
                .codigo("AB@C-12#3!")
                .descricao("Teste")
                .valorDesconto(new BigDecimal("10.00"))
                .dataExpiracao(LocalDate.now().plusDays(30))
                .build();

        when(cupomRepositorio.existePorCodigoEExcluidoFalso("ABC123")).thenReturn(false);
        when(cupomRepositorio.save(any(Cupom.class))).thenReturn(cupomExemplo);

        CupomResposta resposta = cupomServico.criarCupom(requisicao);

        assertThat(resposta.getCodigo()).isEqualTo("ABC123");
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar cupom com código duplicado")
    void deveLancarExcecaoCodigoDuplicado() {
        when(cupomRepositorio.existePorCodigoEExcluidoFalso("ABC123")).thenReturn(true);

        assertThatThrownBy(() -> cupomServico.criarCupom(requisicaoExemplo))
                .isInstanceOf(CodigoCupomDuplicadoException.class);

        verify(cupomRepositorio, never()).save(any(Cupom.class));
    }

    @Test
    @DisplayName("Deve lançar exceção com data passada")
    void deveLancarExcecaoDataPassada() {
        CupomRequisicao requisicao = CupomRequisicao.builder()
                .codigo("ABC123")
                .descricao("Teste")
                .valorDesconto(new BigDecimal("10.00"))
                .dataExpiracao(LocalDate.now().minusDays(1))
                .build();

        when(cupomRepositorio.existePorCodigoEExcluidoFalso(anyString())).thenReturn(false);

        assertThatThrownBy(() -> cupomServico.criarCupom(requisicao))
                .isInstanceOf(CupomInvalidoException.class);
    }

    @Test
    @DisplayName("Deve lançar exceção com valor inválido")
    void deveLancarExcecaoValorInvalido() {
        CupomRequisicao requisicao = CupomRequisicao.builder()
                .codigo("ABC123")
                .descricao("Teste")
                .valorDesconto(new BigDecimal("0.3"))
                .dataExpiracao(LocalDate.now().plusDays(30))
                .build();

        when(cupomRepositorio.existePorCodigoEExcluidoFalso(anyString())).thenReturn(false);

        assertThatThrownBy(() -> cupomServico.criarCupom(requisicao))
                .isInstanceOf(CupomInvalidoException.class);
    }

    @Test
    @DisplayName("Deve buscar todos cupons ativos")
    void deveBuscarTodosCuponsAtivos() {
        when(cupomRepositorio.buscarTodosAtivos()).thenReturn(Arrays.asList(cupomExemplo));

        List<CupomResposta> cupons = cupomServico.obterTodosCuponsAtivos();

        assertThat(cupons).hasSize(1);
        verify(cupomRepositorio, times(1)).buscarTodosAtivos();
    }

    @Test
    @DisplayName("Deve buscar cupom por ID")
    void deveBuscarPorId() {
        when(cupomRepositorio.findById(1L)).thenReturn(Optional.of(cupomExemplo));

        CupomResposta resposta = cupomServico.obterCupomPorId(1L);

        assertThat(resposta.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar ID inexistente")
    void deveLancarExcecaoIdInexistente() {
        when(cupomRepositorio.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cupomServico.obterCupomPorId(999L))
                .isInstanceOf(CupomNaoEncontradoException.class);
    }

    @Test
    @DisplayName("Deve atualizar cupom")
    void deveAtualizarCupom() {
        when(cupomRepositorio.findById(1L)).thenReturn(Optional.of(cupomExemplo));
        when(cupomRepositorio.save(any(Cupom.class))).thenReturn(cupomExemplo);

        CupomResposta resposta = cupomServico.atualizarCupom(1L, requisicaoExemplo);

        assertThat(resposta).isNotNull();
        verify(cupomRepositorio, times(1)).save(any(Cupom.class));
    }

    @Test
    @DisplayName("Deve deletar cupom (soft delete)")
    void deveDeletarCupom() {
        when(cupomRepositorio.findById(1L)).thenReturn(Optional.of(cupomExemplo));

        cupomServico.excluirCupom(1L);

        verify(cupomRepositorio, times(1)).save(any(Cupom.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar cupom já deletado")
    void deveLancarExcecaoCupomJaDeletado() {
        cupomExemplo.setExcluido(true);
        when(cupomRepositorio.findById(1L)).thenReturn(Optional.of(cupomExemplo));

        assertThatThrownBy(() -> cupomServico.excluirCupom(1L))
                .isInstanceOf(CupomJaExcluidoException.class);
    }

    @Test
    @DisplayName("Deve publicar cupom")
    void devePublicarCupom() {
        when(cupomRepositorio.findById(1L)).thenReturn(Optional.of(cupomExemplo));
        when(cupomRepositorio.save(any(Cupom.class))).thenReturn(cupomExemplo);

        CupomResposta resposta = cupomServico.publicarCupom(1L);

        assertThat(resposta).isNotNull();
    }

    @Test
    @DisplayName("Deve despublicar cupom")
    void deveDespublicarCupom() {
        cupomExemplo.setPublicado(true);
        when(cupomRepositorio.findById(1L)).thenReturn(Optional.of(cupomExemplo));
        when(cupomRepositorio.save(any(Cupom.class))).thenReturn(cupomExemplo);

        CupomResposta resposta = cupomServico.despublicarCupom(1L);

        assertThat(resposta).isNotNull();
    }

    @Test
    @DisplayName("Deve buscar cupom por código")
    void deveBuscarPorCodigo() {
        when(cupomRepositorio.buscarPorCodigoEExcluidoFalso("ABC123")).thenReturn(Optional.of(cupomExemplo));

        CupomResposta resposta = cupomServico.obterCupomPorCodigo("ABC-123");

        assertThat(resposta.getCodigo()).isEqualTo("ABC123");
        verify(cupomRepositorio, times(1)).buscarPorCodigoEExcluidoFalso("ABC123");
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar código inexistente")
    void deveLancarExcecaoCodigoInexistente() {
        when(cupomRepositorio.buscarPorCodigoEExcluidoFalso("ABC123")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cupomServico.obterCupomPorCodigo("ABC123"))
                .isInstanceOf(CupomNaoEncontradoException.class)
                .hasMessageContaining("Cupom não encontrado com código: ABC123");
    }

    @Test
    @DisplayName("Deve atualizar cupom deletado e lançar exceção")
    void deveLancarExcecaoAtualizarCupomDeletado() {
        cupomExemplo.setExcluido(true);
        when(cupomRepositorio.findById(1L)).thenReturn(Optional.of(cupomExemplo));

        assertThatThrownBy(() -> cupomServico.atualizarCupom(1L, requisicaoExemplo))
                .isInstanceOf(CupomInvalidoException.class)
                .hasMessageContaining("Não é possível atualizar um cupom deletado");
    }

    @Test
    @DisplayName("Deve atualizar cupom sem nenhum campo informado")
    void deveAtualizarCupomSemCampos() {
        CupomRequisicao requisicaoVazia = CupomRequisicao.builder()
                .codigo(null)
                .descricao(null)
                .valorDesconto(null)
                .dataExpiracao(null)
                .publicado(null)
                .build();

        when(cupomRepositorio.findById(1L)).thenReturn(Optional.of(cupomExemplo));
        when(cupomRepositorio.save(any(Cupom.class))).thenReturn(cupomExemplo);

        CupomResposta resposta = cupomServico.atualizarCupom(1L, requisicaoVazia);

        assertThat(resposta).isNotNull();
        verify(cupomRepositorio, times(1)).save(any(Cupom.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar com valor inválido")
    void deveLancarExcecaoAtualizarValorInvalido() {
        CupomRequisicao requisicao = CupomRequisicao.builder()
                .valorDesconto(new BigDecimal("0.3"))
                .build();

        when(cupomRepositorio.findById(1L)).thenReturn(Optional.of(cupomExemplo));

        assertThatThrownBy(() -> cupomServico.atualizarCupom(1L, requisicao))
                .isInstanceOf(CupomInvalidoException.class);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar com data inválida")
    void deveLancarExcecaoAtualizarDataInvalida() {
        CupomRequisicao requisicao = CupomRequisicao.builder()
                .dataExpiracao(LocalDate.now().minusDays(1))
                .build();

        when(cupomRepositorio.findById(1L)).thenReturn(Optional.of(cupomExemplo));

        assertThatThrownBy(() -> cupomServico.atualizarCupom(1L, requisicao))
                .isInstanceOf(CupomInvalidoException.class);
    }

    @Test
    @DisplayName("Deve lançar exceção ao publicar cupom deletado")
    void deveLancarExcecaoPublicarCupomDeletado() {
        cupomExemplo.setExcluido(true);
        when(cupomRepositorio.findById(1L)).thenReturn(Optional.of(cupomExemplo));

        assertThatThrownBy(() -> cupomServico.publicarCupom(1L))
                .isInstanceOf(CupomInvalidoException.class)
                .hasMessageContaining("Não é possível publicar um cupom deletado");
    }

    @Test
    @DisplayName("Deve criar cupom com publicado=true quando informado")
    void deveCriarCupomPublicadoTrue() {
        CupomRequisicao requisicao = CupomRequisicao.builder()
                .codigo("ABC123")
                .descricao("Teste")
                .valorDesconto(new BigDecimal("10.00"))
                .dataExpiracao(LocalDate.now().plusDays(30))
                .publicado(true)
                .build();

        when(cupomRepositorio.existePorCodigoEExcluidoFalso("ABC123")).thenReturn(false);
        when(cupomRepositorio.save(any(Cupom.class))).thenReturn(cupomExemplo);

        CupomResposta resposta = cupomServico.criarCupom(requisicao);

        assertThat(resposta).isNotNull();
    }

    @Test
    @DisplayName("Deve lançar exceção ao despublicar cupom inexistente")
    void deveLancarExcecaoDespublicarCupomInexistente() {
        when(cupomRepositorio.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cupomServico.despublicarCupom(999L))
                .isInstanceOf(CupomNaoEncontradoException.class);
    }

    @Test
    @DisplayName("Deve lançar exceção ao publicar cupom inexistente")
    void deveLancarExcecaoPublicarCupomInexistente() {
        when(cupomRepositorio.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cupomServico.publicarCupom(999L))
                .isInstanceOf(CupomNaoEncontradoException.class);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar cupom inexistente")
    void deveLancarExcecaoDeletarCupomInexistente() {
        when(cupomRepositorio.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cupomServico.excluirCupom(999L))
                .isInstanceOf(CupomNaoEncontradoException.class);
    }

    @Test
    @DisplayName("Deve atualizar apenas descrição do cupom")
    void deveAtualizarApenasDescricao() {
        CupomRequisicao requisicao = CupomRequisicao.builder()
                .descricao("Nova descrição")
                .build();

        when(cupomRepositorio.findById(1L)).thenReturn(Optional.of(cupomExemplo));
        when(cupomRepositorio.save(any(Cupom.class))).thenReturn(cupomExemplo);

        CupomResposta resposta = cupomServico.atualizarCupom(1L, requisicao);

        assertThat(resposta).isNotNull();
        verify(cupomRepositorio, times(1)).save(any(Cupom.class));
    }

    @Test
    @DisplayName("Deve atualizar apenas valor de desconto")
    void deveAtualizarApenasValorDesconto() {
        CupomRequisicao requisicao = CupomRequisicao.builder()
                .valorDesconto(new BigDecimal("20.00"))
                .build();

        when(cupomRepositorio.findById(1L)).thenReturn(Optional.of(cupomExemplo));
        when(cupomRepositorio.save(any(Cupom.class))).thenReturn(cupomExemplo);

        CupomResposta resposta = cupomServico.atualizarCupom(1L, requisicao);

        assertThat(resposta).isNotNull();
        verify(cupomRepositorio, times(1)).save(any(Cupom.class));
    }

    @Test
    @DisplayName("Deve atualizar apenas data de expiração")
    void deveAtualizarApenasDataExpiracao() {
        CupomRequisicao requisicao = CupomRequisicao.builder()
                .dataExpiracao(LocalDate.now().plusDays(60))
                .build();

        when(cupomRepositorio.findById(1L)).thenReturn(Optional.of(cupomExemplo));
        when(cupomRepositorio.save(any(Cupom.class))).thenReturn(cupomExemplo);

        CupomResposta resposta = cupomServico.atualizarCupom(1L, requisicao);

        assertThat(resposta).isNotNull();
        verify(cupomRepositorio, times(1)).save(any(Cupom.class));
    }

    @Test
    @DisplayName("Deve atualizar apenas status de publicação")
    void deveAtualizarApenasStatusPublicacao() {
        CupomRequisicao requisicao = CupomRequisicao.builder()
                .publicado(true)
                .build();

        when(cupomRepositorio.findById(1L)).thenReturn(Optional.of(cupomExemplo));
        when(cupomRepositorio.save(any(Cupom.class))).thenReturn(cupomExemplo);

        CupomResposta resposta = cupomServico.atualizarCupom(1L, requisicao);

        assertThat(resposta).isNotNull();
        verify(cupomRepositorio, times(1)).save(any(Cupom.class));
    }

    @Test
    @DisplayName("Deve converter cupom para resposta com todos os campos")
    void deveConverterCupomParaResposta() {
        cupomExemplo.setPublicado(true);
        cupomExemplo.setExcluido(false);

        when(cupomRepositorio.findById(1L)).thenReturn(Optional.of(cupomExemplo));

        CupomResposta resposta = cupomServico.obterCupomPorId(1L);

        assertThat(resposta.getId()).isEqualTo(cupomExemplo.getId());
        assertThat(resposta.getCodigo()).isEqualTo(cupomExemplo.getCodigo());
        assertThat(resposta.getDescricao()).isEqualTo(cupomExemplo.getDescricao());
        assertThat(resposta.getValorDesconto()).isEqualTo(cupomExemplo.getValorDesconto());
        assertThat(resposta.getDataExpiracao()).isEqualTo(cupomExemplo.getDataExpiracao());
        assertThat(resposta.getPublicado()).isEqualTo(cupomExemplo.getPublicado());
        assertThat(resposta.getExcluido()).isEqualTo(cupomExemplo.getExcluido());
        assertThat(resposta.getCriadoEm()).isEqualTo(cupomExemplo.getCriadoEm());
        assertThat(resposta.getAtualizadoEm()).isEqualTo(cupomExemplo.getAtualizadoEm());
    }

    @Test
    @DisplayName("Deve buscar cupom e verificar se está ativo")
    void deveBuscarCupomEVerificarAtivo() {
        cupomExemplo.setPublicado(true);
        cupomExemplo.setExcluido(false);

        when(cupomRepositorio.findById(1L)).thenReturn(Optional.of(cupomExemplo));

        CupomResposta resposta = cupomServico.obterCupomPorId(1L);

        assertThat(resposta.getAtivo()).isNotNull();
    }
}
