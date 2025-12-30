package com.cupom.api.service;

import com.cupom.api.dto.CupomRequest;
import com.cupom.api.dto.CupomResponse;
import com.cupom.api.entity.Cupom;
import com.cupom.api.exception.*;
import com.cupom.api.repository.CupomRepository;
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
class CupomServiceTest {

    @Mock
    private CupomRepository cupomRepository;

    @InjectMocks
    private CupomService cupomService;

    private Cupom cupomExemplo;
    private CupomRequest requisicaoExemplo;

    @BeforeEach
    void setUp() {
        cupomExemplo = Cupom.builder()
                .id(1L)
                .code("ABC123")
                .description("Desconto de 10%")
                .discountValue(new BigDecimal("10.00"))
                .expirationDate(LocalDate.now().plusDays(30))
                .published(false)
                .deleted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        requisicaoExemplo = CupomRequest.builder()
                .code("ABC-123")
                .description("Desconto de 10%")
                .discountValue(new BigDecimal("10.00"))
                .expirationDate(LocalDate.now().plusDays(30))
                .published(false)
                .build();
    }

    @Test
    @DisplayName("Deve criar cupom com sucesso")
    void deveCriarCupomComSucesso() {
        when(cupomRepository.existsByCodeAndNotDeleted(anyString())).thenReturn(false);
        when(cupomRepository.save(any(Cupom.class))).thenReturn(cupomExemplo);

        CupomResponse response = cupomService.createCupom(requisicaoExemplo);

        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo("ABC123");
        verify(cupomRepository, times(1)).save(any(Cupom.class));
    }

    @Test
    @DisplayName("Deve normalizar código removendo caracteres especiais")
    void deveNormalizarCodigo() {
        CupomRequest request = CupomRequest.builder()
                .code("AB@C-12#3!")
                .description("Teste")
                .discountValue(new BigDecimal("10.00"))
                .expirationDate(LocalDate.now().plusDays(30))
                .build();

        when(cupomRepository.existsByCodeAndNotDeleted("ABC123")).thenReturn(false);
        when(cupomRepository.save(any(Cupom.class))).thenReturn(cupomExemplo);

        CupomResponse response = cupomService.createCupom(request);

        assertThat(response.getCode()).isEqualTo("ABC123");
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar cupom com código duplicado")
    void deveLancarExcecaoCodigoDuplicado() {
        when(cupomRepository.existsByCodeAndNotDeleted("ABC123")).thenReturn(true);

        assertThatThrownBy(() -> cupomService.createCupom(requisicaoExemplo))
                .isInstanceOf(DuplicateCupomCodeException.class);

        verify(cupomRepository, never()).save(any(Cupom.class));
    }

    @Test
    @DisplayName("Deve lançar exceção com data passada")
    void deveLancarExcecaoDataPassada() {
        CupomRequest request = CupomRequest.builder()
                .code("ABC123")
                .description("Teste")
                .discountValue(new BigDecimal("10.00"))
                .expirationDate(LocalDate.now().minusDays(1))
                .build();

        when(cupomRepository.existsByCodeAndNotDeleted(anyString())).thenReturn(false);

        assertThatThrownBy(() -> cupomService.createCupom(request))
                .isInstanceOf(InvalidCupomException.class);
    }

    @Test
    @DisplayName("Deve lançar exceção com valor inválido")
    void deveLancarExcecaoValorInvalido() {
        CupomRequest request = CupomRequest.builder()
                .code("ABC123")
                .description("Teste")
                .discountValue(new BigDecimal("0.3"))
                .expirationDate(LocalDate.now().plusDays(30))
                .build();

        when(cupomRepository.existsByCodeAndNotDeleted(anyString())).thenReturn(false);

        assertThatThrownBy(() -> cupomService.createCupom(request))
                .isInstanceOf(InvalidCupomException.class);
    }

    @Test
    @DisplayName("Deve buscar todos cupons ativos")
    void deveBuscarTodosCuponsAtivos() {
        when(cupomRepository.findAllActive()).thenReturn(Arrays.asList(cupomExemplo));

        List<CupomResponse> cupons = cupomService.getAllActiveCupons();

        assertThat(cupons).hasSize(1);
        verify(cupomRepository, times(1)).findAllActive();
    }

    @Test
    @DisplayName("Deve buscar cupom por ID")
    void deveBuscarPorId() {
        when(cupomRepository.findById(1L)).thenReturn(Optional.of(cupomExemplo));

        CupomResponse response = cupomService.getCupomById(1L);

        assertThat(response.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar ID inexistente")
    void deveLancarExcecaoIdInexistente() {
        when(cupomRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cupomService.getCupomById(999L))
                .isInstanceOf(CupomNotFoundException.class);
    }

    @Test
    @DisplayName("Deve atualizar cupom")
    void deveAtualizarCupom() {
        when(cupomRepository.findById(1L)).thenReturn(Optional.of(cupomExemplo));
        when(cupomRepository.save(any(Cupom.class))).thenReturn(cupomExemplo);

        CupomResponse response = cupomService.updateCupom(1L, requisicaoExemplo);

        assertThat(response).isNotNull();
        verify(cupomRepository, times(1)).save(any(Cupom.class));
    }

    @Test
    @DisplayName("Deve deletar cupom (soft delete)")
    void deveDeletarCupom() {
        when(cupomRepository.findById(1L)).thenReturn(Optional.of(cupomExemplo));

        cupomService.deleteCupom(1L);

        verify(cupomRepository, times(1)).save(any(Cupom.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar cupom já deletado")
    void deveLancarExcecaoCupomJaDeletado() {
        cupomExemplo.setDeleted(true);
        when(cupomRepository.findById(1L)).thenReturn(Optional.of(cupomExemplo));

        assertThatThrownBy(() -> cupomService.deleteCupom(1L))
                .isInstanceOf(CupomAlreadyDeletedException.class);
    }

    @Test
    @DisplayName("Deve publicar cupom")
    void devePublicarCupom() {
        when(cupomRepository.findById(1L)).thenReturn(Optional.of(cupomExemplo));
        when(cupomRepository.save(any(Cupom.class))).thenReturn(cupomExemplo);

        CupomResponse response = cupomService.publishCupom(1L);

        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("Deve despublicar cupom")
    void deveDespublicarCupom() {
        cupomExemplo.setPublished(true);
        when(cupomRepository.findById(1L)).thenReturn(Optional.of(cupomExemplo));
        when(cupomRepository.save(any(Cupom.class))).thenReturn(cupomExemplo);

        CupomResponse response = cupomService.unpublishCupom(1L);

        assertThat(response).isNotNull();
    }
}
