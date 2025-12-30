package com.cupom.api.service;

import com.cupom.api.dto.CupomRequest;
import com.cupom.api.dto.CupomResponse;
import com.cupom.api.entity.Cupom;
import com.cupom.api.exception.CupomAlreadyDeletedException;
import com.cupom.api.exception.CupomNotFoundException;
import com.cupom.api.exception.DuplicateCupomCodeException;
import com.cupom.api.exception.InvalidCupomException;
import com.cupom.api.repository.CupomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço de gerenciamento de cupons.
 * Encapsula toda a lógica de negócio relacionada a cupons.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CupomService {

    private final CupomRepository cupomRepository;

    /**
     * Cria um novo cupom.
     * REGRAS DE NEGÓCIO:
     * - Normaliza o código (remove caracteres especiais, garante 6 caracteres)
     * - Valida data de expiração (não pode ser no passado)
     * - Valida valor de desconto (mínimo 0.5)
     * - Verifica código duplicado
     * - Pode ser criado como já publicado
     */
    @Transactional
    public CupomResponse createCupom(CupomRequest request) {
        log.info("Criando cupom com código: {}", request.getCode());

        try {
            // REGRA: Normaliza código (remove caracteres especiais, 6 caracteres)
            String normalizedCode = Cupom.normalizeCode(request.getCode());

            // REGRA: Verifica código duplicado
            if (cupomRepository.existsByCodeAndNotDeleted(normalizedCode)) {
                throw new DuplicateCupomCodeException(
                    "Já existe um cupom ativo com o código: " + normalizedCode
                );
            }

            // REGRA: Valida data de expiração
            Cupom.validateExpirationDate(request.getExpirationDate());

            // REGRA: Valida valor de desconto
            Cupom.validateDiscountValue(request.getDiscountValue());

            // Cria o cupom
            Cupom cupom = Cupom.builder()
                    .code(normalizedCode)
                    .description(request.getDescription())
                    .discountValue(request.getDiscountValue())
                    .expirationDate(request.getExpirationDate())
                    .published(request.getPublished() != null ? request.getPublished() : false)
                    .deleted(false)
                    .build();

            cupom = cupomRepository.save(cupom);
            log.info("Cupom criado com sucesso. ID: {}, Código: {}", cupom.getId(), cupom.getCode());

            return mapToResponse(cupom);

        } catch (IllegalArgumentException e) {
            log.error("Erro ao criar cupom: {}", e.getMessage());
            throw new InvalidCupomException(e.getMessage());
        }
    }

    /**
     * Busca todos os cupons ativos (não deletados)
     */
    @Transactional(readOnly = true)
    public List<CupomResponse> getAllActiveCupons() {
        log.info("Buscando todos os cupons ativos");
        return cupomRepository.findAllActive().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Busca cupom por ID
     */
    @Transactional(readOnly = true)
    public CupomResponse getCupomById(Long id) {
        log.info("Buscando cupom por ID: {}", id);
        Cupom cupom = cupomRepository.findById(id)
                .orElseThrow(() -> new CupomNotFoundException("Cupom não encontrado com ID: " + id));

        return mapToResponse(cupom);
    }

    /**
     * Busca cupom por código (apenas ativos)
     */
    @Transactional(readOnly = true)
    public CupomResponse getCupomByCode(String code) {
        log.info("Buscando cupom por código: {}", code);
        
        String normalizedCode = Cupom.normalizeCode(code);
        Cupom cupom = cupomRepository.findByCodeAndNotDeleted(normalizedCode)
                .orElseThrow(() -> new CupomNotFoundException(
                    "Cupom não encontrado com código: " + normalizedCode
                ));

        return mapToResponse(cupom);
    }

    /**
     * Atualiza um cupom existente
     */
    @Transactional
    public CupomResponse updateCupom(Long id, CupomRequest request) {
        log.info("Atualizando cupom ID: {}", id);

        Cupom cupom = cupomRepository.findById(id)
                .orElseThrow(() -> new CupomNotFoundException("Cupom não encontrado com ID: " + id));

        if (cupom.getDeleted()) {
            throw new InvalidCupomException("Não é possível atualizar um cupom deletado");
        }

        try {
            // Atualiza campos
            if (request.getDescription() != null) {
                cupom.setDescription(request.getDescription());
            }

            if (request.getDiscountValue() != null) {
                Cupom.validateDiscountValue(request.getDiscountValue());
                cupom.setDiscountValue(request.getDiscountValue());
            }

            if (request.getExpirationDate() != null) {
                Cupom.validateExpirationDate(request.getExpirationDate());
                cupom.setExpirationDate(request.getExpirationDate());
            }

            if (request.getPublished() != null) {
                cupom.setPublished(request.getPublished());
            }

            cupom = cupomRepository.save(cupom);
            log.info("Cupom atualizado com sucesso. ID: {}", cupom.getId());

            return mapToResponse(cupom);

        } catch (IllegalArgumentException e) {
            log.error("Erro ao atualizar cupom: {}", e.getMessage());
            throw new InvalidCupomException(e.getMessage());
        }
    }

    /**
     * Deleta um cupom (soft delete).
     * REGRA DE NEGÓCIO:
     * - Soft delete: não perde informações
     * - Não pode deletar cupom já deletado
     */
    @Transactional
    public void deleteCupom(Long id) {
        log.info("Deletando cupom ID: {}", id);

        Cupom cupom = cupomRepository.findById(id)
                .orElseThrow(() -> new CupomNotFoundException("Cupom não encontrado com ID: " + id));

        try {
            // REGRA: Não pode deletar cupom já deletado
            cupom.softDelete();
            cupomRepository.save(cupom);
            log.info("Cupom deletado com sucesso (soft delete). ID: {}", cupom.getId());

        } catch (IllegalStateException e) {
            log.error("Erro ao deletar cupom: {}", e.getMessage());
            throw new CupomAlreadyDeletedException(e.getMessage());
        }
    }

    /**
     * Publica um cupom
     */
    @Transactional
    public CupomResponse publishCupom(Long id) {
        log.info("Publicando cupom ID: {}", id);

        Cupom cupom = cupomRepository.findById(id)
                .orElseThrow(() -> new CupomNotFoundException("Cupom não encontrado com ID: " + id));

        if (cupom.getDeleted()) {
            throw new InvalidCupomException("Não é possível publicar um cupom deletado");
        }

        cupom.publish();
        cupom = cupomRepository.save(cupom);
        log.info("Cupom publicado com sucesso. ID: {}", cupom.getId());

        return mapToResponse(cupom);
    }

    /**
     * Despublica um cupom
     */
    @Transactional
    public CupomResponse unpublishCupom(Long id) {
        log.info("Despublicando cupom ID: {}", id);

        Cupom cupom = cupomRepository.findById(id)
                .orElseThrow(() -> new CupomNotFoundException("Cupom não encontrado com ID: " + id));

        cupom.unpublish();
        cupom = cupomRepository.save(cupom);
        log.info("Cupom despublicado com sucesso. ID: {}", cupom.getId());

        return mapToResponse(cupom);
    }

    /**
     * Mapeia entidade para DTO de resposta
     */
    private CupomResponse mapToResponse(Cupom cupom) {
        return CupomResponse.builder()
                .id(cupom.getId())
                .code(cupom.getCode())
                .description(cupom.getDescription())
                .discountValue(cupom.getDiscountValue())
                .expirationDate(cupom.getExpirationDate())
                .published(cupom.getPublished())
                .deleted(cupom.getDeleted())
                .active(cupom.isActive())
                .expired(cupom.isExpired())
                .createdAt(cupom.getCreatedAt())
                .updatedAt(cupom.getUpdatedAt())
                .deletedAt(cupom.getDeletedAt())
                .build();
    }
}
