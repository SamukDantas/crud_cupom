package com.cupom.api.controller;

import com.cupom.api.domain.Cupom;
import com.cupom.api.dto.CupomRequisicao;
import com.cupom.api.repository.CupomRepositorio;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integração para o CupomControlador
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CupomControladorTesteIntegracao {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CupomRepositorio cupomRepositorio;

    @BeforeEach
    void setUp() {
        cupomRepositorio.deleteAll();
    }

    @Test
    void deveObterTodosCupons() throws Exception {
        mockMvc.perform(get("/api/cupons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(empty())));
    }

    @Test
    void deveCriarCupom() throws Exception {
        CupomRequisicao requisicao = new CupomRequisicao();
        requisicao.setCodigo("ABC123");
        requisicao.setDescricao("Cupom de teste");
        requisicao.setValorDesconto(BigDecimal.valueOf(10.00));
        requisicao.setDataExpiracao(LocalDate.now().plusDays(30));
        requisicao.setPublicado(false);

        mockMvc.perform(post("/api/cupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requisicao)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigo", is("ABC123")))
                .andExpect(jsonPath("$.descricao", is("Cupom de teste")))
                .andExpect(jsonPath("$.valorDesconto", is(10.0)))
                .andExpect(jsonPath("$.publicado", is(false)));
    }

    @Test
    void deveObterCupomPorId() throws Exception {
        Cupom cupom = new Cupom();
        cupom.setCodigo("ABC123");
        cupom.setDescricao("Cupom de teste");
        cupom.setValorDesconto(BigDecimal.valueOf(10.00));
        cupom.setDataExpiracao(LocalDate.now().plusDays(30));
        cupom.setPublicado(false);
        cupom = cupomRepositorio.save(cupom);

        mockMvc.perform(get("/api/cupons/" + cupom.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo", is("ABC123")));
    }

    @Test
    void deveObterCupomPorCodigo() throws Exception {
        Cupom cupom = new Cupom();
        cupom.setCodigo("ABC123");
        cupom.setDescricao("Cupom de teste");
        cupom.setValorDesconto(BigDecimal.valueOf(10.00));
        cupom.setDataExpiracao(LocalDate.now().plusDays(30));
        cupom.setPublicado(false);
        cupomRepositorio.save(cupom);

        mockMvc.perform(get("/api/cupons/codigo/ABC123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo", is("ABC123")));
    }

    @Test
    void deveAtualizarCupom() throws Exception {
        Cupom cupom = new Cupom();
        cupom.setCodigo("ABC123");
        cupom.setDescricao("Cupom de teste");
        cupom.setValorDesconto(BigDecimal.valueOf(10.00));
        cupom.setDataExpiracao(LocalDate.now().plusDays(30));
        cupom.setPublicado(false);
        cupom = cupomRepositorio.save(cupom);

        CupomRequisicao requisicao = new CupomRequisicao();
        requisicao.setCodigo("ABC123");
        requisicao.setDescricao("Cupom atualizado");
        requisicao.setValorDesconto(BigDecimal.valueOf(15.00));
        requisicao.setDataExpiracao(LocalDate.now().plusDays(60));
        requisicao.setPublicado(true);

        mockMvc.perform(put("/api/cupons/" + cupom.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requisicao)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descricao", is("Cupom atualizado")))
                .andExpect(jsonPath("$.valorDesconto", is(15.0)))
                .andExpect(jsonPath("$.publicado", is(true)));
    }

    @Test
    void deveLancarExcecaoAoCriarCupomComCodigoDuplicado() throws Exception {
        Cupom cupom = new Cupom();
        cupom.setCodigo("ABC123");
        cupom.setDescricao("Primeiro cupom");
        cupom.setValorDesconto(BigDecimal.valueOf(10.00));
        cupom.setDataExpiracao(LocalDate.now().plusDays(30));
        cupom.setPublicado(false);
        cupomRepositorio.save(cupom);

        CupomRequisicao requisicao = new CupomRequisicao();
        requisicao.setCodigo("ABC123");
        requisicao.setDescricao("Segundo cupom");
        requisicao.setValorDesconto(BigDecimal.valueOf(20.00));
        requisicao.setDataExpiracao(LocalDate.now().plusDays(60));
        requisicao.setPublicado(false);

        mockMvc.perform(post("/api/cupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requisicao)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensagem", containsString("Já existe um cupom")));
    }

    @Test
    void deveLancarExcecaoAoCriarCupomComDataExpiracaoPassada() throws Exception {
        CupomRequisicao requisicao = new CupomRequisicao();
        requisicao.setCodigo("ABC123");
        requisicao.setDescricao("Cupom com data inválida");
        requisicao.setValorDesconto(BigDecimal.valueOf(10.00));
        requisicao.setDataExpiracao(LocalDate.now().minusDays(1));
        requisicao.setPublicado(false);

        mockMvc.perform(post("/api/cupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requisicao)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem", containsString("Data de expiração não pode ser no passado")));
    }

    @Test
    void deveLancarExcecaoAoCriarCupomComValorDescontoInvalido() throws Exception {
        CupomRequisicao requisicao = new CupomRequisicao();
        requisicao.setCodigo("ABC123");
        requisicao.setDescricao("Cupom com desconto inválido");
        requisicao.setValorDesconto(BigDecimal.valueOf(0.3));
        requisicao.setDataExpiracao(LocalDate.now().plusDays(30));
        requisicao.setPublicado(false);

        mockMvc.perform(post("/api/cupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requisicao)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.valorDesconto", containsString("Valor de desconto deve ser no mínimo 0.5")));
    }

    @Test
    void deveCriarCupomComCodigoComCaracteresEspeciais() throws Exception {
        CupomRequisicao requisicao = new CupomRequisicao();
        requisicao.setCodigo("AB@C-12#3!");
        requisicao.setDescricao("Cupom com caracteres especiais");
        requisicao.setValorDesconto(BigDecimal.valueOf(10.00));
        requisicao.setDataExpiracao(LocalDate.now().plusDays(30));
        requisicao.setPublicado(false);

        mockMvc.perform(post("/api/cupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requisicao)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigo", is("ABC123")));
    }

    @Test
    void deveLancarExcecaoAoBuscarCupomPorIdInexistente() throws Exception {
        mockMvc.perform(get("/api/cupons/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem", containsString("Cupom não encontrado")));
    }

    @Test
    void deveLancarExcecaoAoBuscarCupomPorCodigoInexistente() throws Exception {
        mockMvc.perform(get("/api/cupons/codigo/NAOEXISTE"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem", containsString("Cupom não encontrado")));
    }

    @Test
    void deveExcluirCupom() throws Exception {
        Cupom cupom = new Cupom();
        cupom.setCodigo("ABC123");
        cupom.setDescricao("Cupom de teste");
        cupom.setValorDesconto(BigDecimal.valueOf(10.00));
        cupom.setDataExpiracao(LocalDate.now().plusDays(30));
        cupom.setPublicado(false);
        cupom = cupomRepositorio.save(cupom);

        mockMvc.perform(delete("/api/cupons/" + cupom.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/cupons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(empty())));
    }

    @Test
    void deveObterTodosCuponsComDados() throws Exception {
        Cupom cupom1 = new Cupom();
        cupom1.setCodigo("ABC123");
        cupom1.setDescricao("Cupom 1");
        cupom1.setValorDesconto(BigDecimal.valueOf(10.00));
        cupom1.setDataExpiracao(LocalDate.now().plusDays(30));
        cupom1.setPublicado(false);
        cupomRepositorio.save(cupom1);

        Cupom cupom2 = new Cupom();
        cupom2.setCodigo("DEF456");
        cupom2.setDescricao("Cupom 2");
        cupom2.setValorDesconto(BigDecimal.valueOf(20.00));
        cupom2.setDataExpiracao(LocalDate.now().plusDays(60));
        cupom2.setPublicado(true);
        cupomRepositorio.save(cupom2);

        Cupom cupom3 = new Cupom();
        cupom3.setCodigo("GHI789");
        cupom3.setDescricao("Cupom 3");
        cupom3.setValorDesconto(BigDecimal.valueOf(30.00));
        cupom3.setDataExpiracao(LocalDate.now().plusDays(90));
        cupom3.setPublicado(false);
        cupomRepositorio.save(cupom3);

        mockMvc.perform(get("/api/cupons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    void deveLancarExcecaoAoExcluirCupomJaExcluido() throws Exception {
        Cupom cupom = new Cupom();
        cupom.setCodigo("ABC123");
        cupom.setDescricao("Cupom de teste");
        cupom.setValorDesconto(BigDecimal.valueOf(10.00));
        cupom.setDataExpiracao(LocalDate.now().plusDays(30));
        cupom.setPublicado(false);
        cupom.setExcluido(true);
        cupom = cupomRepositorio.save(cupom);

        mockMvc.perform(delete("/api/cupons/" + cupom.getId()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem", containsString("Cupom já está deletado")));
    }

    @Test
    void deveLancarExcecaoAoAtualizarCupomInexistente() throws Exception {
        CupomRequisicao requisicao = new CupomRequisicao();
        requisicao.setCodigo("ABC123");
        requisicao.setDescricao("Cupom atualizado");
        requisicao.setValorDesconto(BigDecimal.valueOf(15.00));
        requisicao.setDataExpiracao(LocalDate.now().plusDays(30));
        requisicao.setPublicado(true);

        mockMvc.perform(put("/api/cupons/99999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requisicao)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem", containsString("Cupom não encontrado")));
    }

    @Test
    void deveLancarExcecaoAoExcluirCupomInexistente() throws Exception {
        mockMvc.perform(delete("/api/cupons/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem", containsString("Cupom não encontrado")));
    }

    @Test
    void deveLancarExcecaoAoCriarCupomComCodigoVazio() throws Exception {
        CupomRequisicao requisicao = new CupomRequisicao();
        requisicao.setCodigo("");
        requisicao.setDescricao("Cupom de teste");
        requisicao.setValorDesconto(BigDecimal.valueOf(10.00));
        requisicao.setDataExpiracao(LocalDate.now().plusDays(30));
        requisicao.setPublicado(false);

        mockMvc.perform(post("/api/cupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requisicao)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveLancarExcecaoAoCriarCupomComDescricaoNula() throws Exception {
        CupomRequisicao requisicao = new CupomRequisicao();
        requisicao.setCodigo("ABC123");
        requisicao.setDescricao(null);
        requisicao.setValorDesconto(BigDecimal.valueOf(10.00));
        requisicao.setDataExpiracao(LocalDate.now().plusDays(30));
        requisicao.setPublicado(false);

        mockMvc.perform(post("/api/cupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requisicao)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveLancarExcecaoAoCriarCupomComDataExpiracaoNula() throws Exception {
        CupomRequisicao requisicao = new CupomRequisicao();
        requisicao.setCodigo("ABC123");
        requisicao.setDescricao("Cupom de teste");
        requisicao.setValorDesconto(BigDecimal.valueOf(10.00));
        requisicao.setDataExpiracao(null);
        requisicao.setPublicado(false);

        mockMvc.perform(post("/api/cupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requisicao)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveLancarExcecaoAoCriarCupomComDescontoZero() throws Exception {
        CupomRequisicao requisicao = new CupomRequisicao();
        requisicao.setCodigo("ABC123");
        requisicao.setDescricao("Cupom de teste");
        requisicao.setValorDesconto(BigDecimal.ZERO);
        requisicao.setDataExpiracao(LocalDate.now().plusDays(30));
        requisicao.setPublicado(false);

        mockMvc.perform(post("/api/cupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requisicao)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveLancarExcecaoAoCriarCupomComDescontoNegativo() throws Exception {
        CupomRequisicao requisicao = new CupomRequisicao();
        requisicao.setCodigo("ABC123");
        requisicao.setDescricao("Cupom de teste");
        requisicao.setValorDesconto(BigDecimal.valueOf(-5.00));
        requisicao.setDataExpiracao(LocalDate.now().plusDays(30));
        requisicao.setPublicado(false);

        mockMvc.perform(post("/api/cupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requisicao)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveLancarExcecaoAoAtualizarCupomComDadosInvalidos() throws Exception {
        Cupom cupom = new Cupom();
        cupom.setCodigo("ABC123");
        cupom.setDescricao("Cupom de teste");
        cupom.setValorDesconto(BigDecimal.valueOf(10.00));
        cupom.setDataExpiracao(LocalDate.now().plusDays(30));
        cupom.setPublicado(false);
        cupom = cupomRepositorio.save(cupom);

        CupomRequisicao requisicao = new CupomRequisicao();
        requisicao.setCodigo("ABC123");
        requisicao.setDescricao("Cupom atualizado");
        requisicao.setValorDesconto(BigDecimal.valueOf(0.1));
        requisicao.setDataExpiracao(LocalDate.now().plusDays(30));
        requisicao.setPublicado(true);

        mockMvc.perform(put("/api/cupons/" + cupom.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requisicao)))
                .andExpect(status().isBadRequest());
    }
}
