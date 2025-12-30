package com.cupom.api.controller;

import com.cupom.api.dto.CupomRequest;
import com.cupom.api.entity.Cupom;
import com.cupom.api.repository.CupomRepository;
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
 * Testes de integração para o CupomController.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CupomControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CupomRepository cupomRepository;

    @BeforeEach
    void setUp() {
        cupomRepository.deleteAll();
    }

    @Test
    void testGetAllCupons() throws Exception {
        mockMvc.perform(get("/api/cupons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(empty())));
    }

    @Test
    void testCreateCupom() throws Exception {
        CupomRequest request = new CupomRequest();
        request.setCode("ABC123");
        request.setDescription("Cupom de teste");
        request.setDiscountValue(BigDecimal.valueOf(10.00));
        request.setExpirationDate(LocalDate.now().plusDays(30));
        request.setPublished(false);

        mockMvc.perform(post("/api/cupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code", is("ABC123")))
                .andExpect(jsonPath("$.description", is("Cupom de teste")))
                .andExpect(jsonPath("$.discountValue", is(10.0)))
                .andExpect(jsonPath("$.published", is(false)));
    }

    @Test
    void testGetCupomById() throws Exception {
        Cupom cupom = new Cupom();
        cupom.setCode("ABC123");
        cupom.setDescription("Cupom de teste");
        cupom.setDiscountValue(BigDecimal.valueOf(10.00));
        cupom.setExpirationDate(LocalDate.now().plusDays(30));
        cupom.setPublished(false);
        cupom = cupomRepository.save(cupom);

        mockMvc.perform(get("/api/cupons/" + cupom.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("ABC123")));
    }

    @Test
    void testGetCupomByCode() throws Exception {
        Cupom cupom = new Cupom();
        cupom.setCode("ABC123");
        cupom.setDescription("Cupom de teste");
        cupom.setDiscountValue(BigDecimal.valueOf(10.00));
        cupom.setExpirationDate(LocalDate.now().plusDays(30));
        cupom.setPublished(false);
        cupomRepository.save(cupom);

        mockMvc.perform(get("/api/cupons/code/ABC123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("ABC123")));
    }

    @Test
    void testUpdateCupom() throws Exception {
        Cupom cupom = new Cupom();
        cupom.setCode("ABC123");
        cupom.setDescription("Cupom de teste");
        cupom.setDiscountValue(BigDecimal.valueOf(10.00));
        cupom.setExpirationDate(LocalDate.now().plusDays(30));
        cupom.setPublished(false);
        cupom = cupomRepository.save(cupom);

        CupomRequest request = new CupomRequest();
        request.setCode("ABC123");
        request.setDescription("Cupom atualizado");
        request.setDiscountValue(BigDecimal.valueOf(15.00));
        request.setExpirationDate(LocalDate.now().plusDays(60));
        request.setPublished(true);

        mockMvc.perform(put("/api/cupons/" + cupom.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is("Cupom atualizado")))
                .andExpect(jsonPath("$.discountValue", is(15.0)))
                .andExpect(jsonPath("$.published", is(true)));
    }

    @Test
    void testCreateCupomWithDuplicateCode() throws Exception {
        Cupom cupom = new Cupom();
        cupom.setCode("ABC123");
        cupom.setDescription("Primeiro cupom");
        cupom.setDiscountValue(BigDecimal.valueOf(10.00));
        cupom.setExpirationDate(LocalDate.now().plusDays(30));
        cupom.setPublished(false);
        cupomRepository.save(cupom);

        CupomRequest request = new CupomRequest();
        request.setCode("ABC123");
        request.setDescription("Segundo cupom");
        request.setDiscountValue(BigDecimal.valueOf(20.00));
        request.setExpirationDate(LocalDate.now().plusDays(60));
        request.setPublished(false);

        mockMvc.perform(post("/api/cupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("Já existe um cupom")));
    }

    @Test
    void testCreateCupomWithPastExpirationDate() throws Exception {
        CupomRequest request = new CupomRequest();
        request.setCode("ABC123");
        request.setDescription("Cupom com data inválida");
        request.setDiscountValue(BigDecimal.valueOf(10.00));
        request.setExpirationDate(LocalDate.now().minusDays(1));
        request.setPublished(false);

        mockMvc.perform(post("/api/cupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Data de expiração não pode ser no passado")));
    }

    @Test
    void testCreateCupomWithInvalidDiscountValue() throws Exception {
        CupomRequest request = new CupomRequest();
        request.setCode("ABC123");
        request.setDescription("Cupom com desconto inválido");
        request.setDiscountValue(BigDecimal.valueOf(0.3));
        request.setExpirationDate(LocalDate.now().plusDays(30));
        request.setPublished(false);

        mockMvc.perform(post("/api/cupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.discountValue", containsString("Valor de desconto deve ser no mínimo 0.5")));
    }

    @Test
    void testCreateCupomWithCodeSpecialChars() throws Exception {
        CupomRequest request = new CupomRequest();
        request.setCode("AB@C-12#3!");
        request.setDescription("Cupom com caracteres especiais");
        request.setDiscountValue(BigDecimal.valueOf(10.00));
        request.setExpirationDate(LocalDate.now().plusDays(30));
        request.setPublished(false);

        mockMvc.perform(post("/api/cupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code", is("ABC123")));
    }

    @Test
    void testGetCupomByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/cupons/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("Cupom não encontrado")));
    }

    @Test
    void testGetCupomByCodeNotFound() throws Exception {
        mockMvc.perform(get("/api/cupons/code/NOTFOUND"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("Cupom não encontrado")));
    }

    @Test
    void testDeleteCupom() throws Exception {
        Cupom cupom = new Cupom();
        cupom.setCode("ABC123");
        cupom.setDescription("Cupom de teste");
        cupom.setDiscountValue(BigDecimal.valueOf(10.00));
        cupom.setExpirationDate(LocalDate.now().plusDays(30));
        cupom.setPublished(false);
        cupom = cupomRepository.save(cupom);

        mockMvc.perform(delete("/api/cupons/" + cupom.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/cupons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(empty())));
    }

    @Test
    void testGetAllCuponsWithData() throws Exception {
        Cupom cupom1 = new Cupom();
        cupom1.setCode("ABC123");
        cupom1.setDescription("Cupom 1");
        cupom1.setDiscountValue(BigDecimal.valueOf(10.00));
        cupom1.setExpirationDate(LocalDate.now().plusDays(30));
        cupom1.setPublished(false);
        cupomRepository.save(cupom1);

        Cupom cupom2 = new Cupom();
        cupom2.setCode("DEF456");
        cupom2.setDescription("Cupom 2");
        cupom2.setDiscountValue(BigDecimal.valueOf(20.00));
        cupom2.setExpirationDate(LocalDate.now().plusDays(60));
        cupom2.setPublished(true);
        cupomRepository.save(cupom2);

        Cupom cupom3 = new Cupom();
        cupom3.setCode("GHI789");
        cupom3.setDescription("Cupom 3");
        cupom3.setDiscountValue(BigDecimal.valueOf(30.00));
        cupom3.setExpirationDate(LocalDate.now().plusDays(90));
        cupom3.setPublished(false);
        cupomRepository.save(cupom3);

        mockMvc.perform(get("/api/cupons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    void testDeleteAlreadyDeletedCupom() throws Exception {
        Cupom cupom = new Cupom();
        cupom.setCode("ABC123");
        cupom.setDescription("Cupom de teste");
        cupom.setDiscountValue(BigDecimal.valueOf(10.00));
        cupom.setExpirationDate(LocalDate.now().plusDays(30));
        cupom.setPublished(false);
        cupom.setDeleted(true);
        cupom = cupomRepository.save(cupom);

        mockMvc.perform(delete("/api/cupons/" + cupom.getId()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Cupom já está deletado")));
    }

    @Test
    void testUpdateCupomNotFound() throws Exception {
        CupomRequest request = new CupomRequest();
        request.setCode("ABC123");
        request.setDescription("Cupom atualizado");
        request.setDiscountValue(BigDecimal.valueOf(15.00));
        request.setExpirationDate(LocalDate.now().plusDays(30));
        request.setPublished(true);

        mockMvc.perform(put("/api/cupons/99999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("Cupom não encontrado")));
    }

    @Test
    void testDeleteCupomNotFound() throws Exception {
        mockMvc.perform(delete("/api/cupons/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("Cupom não encontrado")));
    }

    @Test
    void testCreateCupomWithEmptyCode() throws Exception {
        CupomRequest request = new CupomRequest();
        request.setCode("");
        request.setDescription("Cupom de teste");
        request.setDiscountValue(BigDecimal.valueOf(10.00));
        request.setExpirationDate(LocalDate.now().plusDays(30));
        request.setPublished(false);

        mockMvc.perform(post("/api/cupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateCupomWithNullDescription() throws Exception {
        CupomRequest request = new CupomRequest();
        request.setCode("ABC123");
        request.setDescription(null);
        request.setDiscountValue(BigDecimal.valueOf(10.00));
        request.setExpirationDate(LocalDate.now().plusDays(30));
        request.setPublished(false);

        mockMvc.perform(post("/api/cupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateCupomWithNullExpirationDate() throws Exception {
        CupomRequest request = new CupomRequest();
        request.setCode("ABC123");
        request.setDescription("Cupom de teste");
        request.setDiscountValue(BigDecimal.valueOf(10.00));
        request.setExpirationDate(null);
        request.setPublished(false);

        mockMvc.perform(post("/api/cupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateCupomWithZeroDiscount() throws Exception {
        CupomRequest request = new CupomRequest();
        request.setCode("ABC123");
        request.setDescription("Cupom de teste");
        request.setDiscountValue(BigDecimal.ZERO);
        request.setExpirationDate(LocalDate.now().plusDays(30));
        request.setPublished(false);

        mockMvc.perform(post("/api/cupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateCupomWithNegativeDiscount() throws Exception {
        CupomRequest request = new CupomRequest();
        request.setCode("ABC123");
        request.setDescription("Cupom de teste");
        request.setDiscountValue(BigDecimal.valueOf(-5.00));
        request.setExpirationDate(LocalDate.now().plusDays(30));
        request.setPublished(false);

        mockMvc.perform(post("/api/cupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateCupomWithInvalidData() throws Exception {
        Cupom cupom = new Cupom();
        cupom.setCode("ABC123");
        cupom.setDescription("Cupom de teste");
        cupom.setDiscountValue(BigDecimal.valueOf(10.00));
        cupom.setExpirationDate(LocalDate.now().plusDays(30));
        cupom.setPublished(false);
        cupom = cupomRepository.save(cupom);

        CupomRequest request = new CupomRequest();
        request.setCode("ABC123");
        request.setDescription("Cupom atualizado");
        request.setDiscountValue(BigDecimal.valueOf(0.1));
        request.setExpirationDate(LocalDate.now().plusDays(30));
        request.setPublished(true);

        mockMvc.perform(put("/api/cupons/" + cupom.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}