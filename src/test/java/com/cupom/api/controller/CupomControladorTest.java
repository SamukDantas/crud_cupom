package com.cupom.api.controller;

import com.cupom.api.dto.CupomRequisicao;
import com.cupom.api.dto.CupomResposta;
import com.cupom.api.service.CupomServico;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CupomControlador.class)
class CupomControladorTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CupomServico cupomServico;

    private ObjectMapper objectMapper;
    private CupomRequisicao requisicao;
    private CupomResposta resposta;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        requisicao = CupomRequisicao.builder()
                .codigo("ABC123")
                .descricao("Cupom de teste")
                .valorDesconto(new BigDecimal("1.0"))
                .dataExpiracao(LocalDate.now().plusDays(30))
                .build();

        resposta = CupomResposta.builder()
                .id(1L)
                .codigo("ABC123")
                .descricao("Cupom de teste")
                .valorDesconto(new BigDecimal("1.0"))
                .dataExpiracao(LocalDate.now().plusDays(30))
                .publicado(false)
                .build();
    }

    @Test
    void deveCriarCupom() throws Exception {
        when(cupomServico.criarCupom(any(CupomRequisicao.class))).thenReturn(resposta);

        mockMvc.perform(post("/api/cupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requisicao)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.codigo").value("ABC123"));
    }

    @Test
    void deveListarTodosCuponsAtivos() throws Exception {
        when(cupomServico.obterTodosCuponsAtivos()).thenReturn(List.of(resposta));

        mockMvc.perform(get("/api/cupons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].codigo").value("ABC123"));
    }

    @Test
    void deveObterCupomPorId() throws Exception {
        when(cupomServico.obterCupomPorId(1L)).thenReturn(resposta);

        mockMvc.perform(get("/api/cupons/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.codigo").value("ABC123"));
    }

    @Test
    void deveObterCupomPorCodigo() throws Exception {
        when(cupomServico.obterCupomPorCodigo("ABC123")).thenReturn(resposta);

        mockMvc.perform(get("/api/cupons/codigo/ABC123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.codigo").value("ABC123"));
    }

    @Test
    void deveAtualizarCupom() throws Exception {
        when(cupomServico.atualizarCupom(eq(1L), any(CupomRequisicao.class))).thenReturn(resposta);

        mockMvc.perform(put("/api/cupons/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requisicao)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.codigo").value("ABC123"));
    }

    @Test
    void deveExcluirCupom() throws Exception {
        doNothing().when(cupomServico).excluirCupom(1L);

        mockMvc.perform(delete("/api/cupons/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void devePublicarCupom() throws Exception {
        CupomResposta respostaPublicada = CupomResposta.builder()
                .id(1L)
                .codigo("ABC123")
                .descricao("Cupom de teste")
                .valorDesconto(new BigDecimal("1.0"))
                .dataExpiracao(LocalDate.now().plusDays(30))
                .publicado(true)
                .build();

        when(cupomServico.publicarCupom(1L)).thenReturn(respostaPublicada);

        mockMvc.perform(post("/api/cupons/1/publicar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.publicado").value(true));
    }

    @Test
    void deveDespublicarCupom() throws Exception {
        when(cupomServico.despublicarCupom(1L)).thenReturn(resposta);

        mockMvc.perform(post("/api/cupons/1/despublicar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.publicado").value(false));
    }
}
