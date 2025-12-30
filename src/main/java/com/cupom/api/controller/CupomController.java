package com.cupom.api.controller;

import com.cupom.api.dto.CupomRequest;
import com.cupom.api.dto.CupomResponse;
import com.cupom.api.service.CupomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller REST para gerenciamento de cupons
 */
@Tag(name = "Cupons", description = "Gerenciamento de cupons de desconto")
@RestController
@RequestMapping("/api/cupons")
@RequiredArgsConstructor
public class CupomController {

    private final CupomService cupomService;

    @Operation(summary = "Criar cupom", description = "Cria um novo cupom de desconto")
    @PostMapping
    public ResponseEntity<CupomResponse> createCupom(@Valid @RequestBody CupomRequest request) {
        CupomResponse response = cupomService.createCupom(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Listar cupons ativos", description = "Lista todos os cupons ativos (não deletados)")
    @GetMapping
    public ResponseEntity<List<CupomResponse>> getAllActiveCupons() {
        List<CupomResponse> cupons = cupomService.getAllActiveCupons();
        return ResponseEntity.ok(cupons);
    }

    @Operation(summary = "Buscar cupom por ID", description = "Retorna um cupom específico pelo ID")
    @GetMapping("/{id}")
    public ResponseEntity<CupomResponse> getCupomById(@PathVariable Long id) {
        CupomResponse cupom = cupomService.getCupomById(id);
        return ResponseEntity.ok(cupom);
    }

    @Operation(summary = "Buscar cupom por código", description = "Retorna um cupom específico pelo código")
    @GetMapping("/code/{code}")
    public ResponseEntity<CupomResponse> getCupomByCode(@PathVariable String code) {
        CupomResponse cupom = cupomService.getCupomByCode(code);
        return ResponseEntity.ok(cupom);
    }

    @Operation(summary = "Atualizar cupom", description = "Atualiza um cupom existente")
    @PutMapping("/{id}")
    public ResponseEntity<CupomResponse> updateCupom(
            @PathVariable Long id,
            @Valid @RequestBody CupomRequest request) {
        CupomResponse cupom = cupomService.updateCupom(id, request);
        return ResponseEntity.ok(cupom);
    }

    @Operation(summary = "Deletar cupom", description = "Deleta um cupom (soft delete)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCupom(@PathVariable Long id) {
        cupomService.deleteCupom(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Publicar cupom", description = "Publica um cupom")
    @PostMapping("/{id}/publish")
    public ResponseEntity<CupomResponse> publishCupom(@PathVariable Long id) {
        CupomResponse cupom = cupomService.publishCupom(id);
        return ResponseEntity.ok(cupom);
    }

    @Operation(summary = "Despublicar cupom", description = "Despublica um cupom")
    @PostMapping("/{id}/unpublish")
    public ResponseEntity<CupomResponse> unpublishCupom(@PathVariable Long id) {
        CupomResponse cupom = cupomService.unpublishCupom(id);
        return ResponseEntity.ok(cupom);
    }
}
