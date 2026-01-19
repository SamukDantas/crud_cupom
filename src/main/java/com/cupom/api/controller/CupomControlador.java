package com.cupom.api.controller;

import com.cupom.api.dto.CupomRequisicao;
import com.cupom.api.dto.CupomResposta;
import com.cupom.api.service.CupomServico;
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
 * Controlador REST para gerenciamento de cupons
 */
@Tag(name = "Cupons", description = "Gerenciamento de cupons de desconto")
@RestController
@RequestMapping("/api/cupons")
@RequiredArgsConstructor
public class CupomControlador {

    private final CupomServico cupomServico;

    @Operation(summary = "Criar cupom", description = "Cria um novo cupom de desconto")
    @PostMapping
    public ResponseEntity<CupomResposta> criarCupom(@Valid @RequestBody CupomRequisicao requisicao) {
        CupomResposta resposta = cupomServico.criarCupom(requisicao);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @Operation(summary = "Listar cupons ativos", description = "Lista todos os cupons ativos (não deletados)")
    @GetMapping
    public ResponseEntity<List<CupomResposta>> obterTodosCuponsAtivos() {
        List<CupomResposta> cupons = cupomServico.obterTodosCuponsAtivos();
        return ResponseEntity.ok(cupons);
    }

    @Operation(summary = "Buscar cupom por ID", description = "Retorna um cupom específico pelo ID")
    @GetMapping("/{id}")
    public ResponseEntity<CupomResposta> obterCupomPorId(@PathVariable Long id) {
        CupomResposta cupom = cupomServico.obterCupomPorId(id);
        return ResponseEntity.ok(cupom);
    }

    @Operation(summary = "Buscar cupom por código", description = "Retorna um cupom específico pelo código")
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<CupomResposta> obterCupomPorCodigo(@PathVariable String codigo) {
        CupomResposta cupom = cupomServico.obterCupomPorCodigo(codigo);
        return ResponseEntity.ok(cupom);
    }

    @Operation(summary = "Atualizar cupom", description = "Atualiza um cupom existente")
    @PutMapping("/{id}")
    public ResponseEntity<CupomResposta> atualizarCupom(
            @PathVariable Long id,
            @Valid @RequestBody CupomRequisicao requisicao) {
        CupomResposta cupom = cupomServico.atualizarCupom(id, requisicao);
        return ResponseEntity.ok(cupom);
    }

    @Operation(summary = "Deletar cupom", description = "Deleta um cupom (soft delete)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirCupom(@PathVariable Long id) {
        cupomServico.excluirCupom(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Publicar cupom", description = "Publica um cupom")
    @PostMapping("/{id}/publicar")
    public ResponseEntity<CupomResposta> publicarCupom(@PathVariable Long id) {
        CupomResposta cupom = cupomServico.publicarCupom(id);
        return ResponseEntity.ok(cupom);
    }

    @Operation(summary = "Despublicar cupom", description = "Despublica um cupom")
    @PostMapping("/{id}/despublicar")
    public ResponseEntity<CupomResposta> despublicarCupom(@PathVariable Long id) {
        CupomResposta cupom = cupomServico.despublicarCupom(id);
        return ResponseEntity.ok(cupom);
    }
}
