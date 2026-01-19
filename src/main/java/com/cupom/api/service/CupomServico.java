package com.cupom.api.service;

import com.cupom.api.domain.Cupom;
import com.cupom.api.dto.CupomRequisicao;
import com.cupom.api.dto.CupomResposta;
import com.cupom.api.exception.CodigoCupomDuplicadoException;
import com.cupom.api.exception.CupomInvalidoException;
import com.cupom.api.exception.CupomJaExcluidoException;
import com.cupom.api.exception.CupomNaoEncontradoException;
import com.cupom.api.repository.CupomRepositorio;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço de gerenciamento de cupons
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CupomServico {

    private final CupomRepositorio cupomRepositorio;

    /**
     * Cria um novo cupom
     */
    @Transactional
    public CupomResposta criarCupom(CupomRequisicao requisicao) {
        log.info("Criando cupom com código: {}", requisicao.getCodigo());

        try {
            String codigoNormalizado = Cupom.normalizarCodigo(requisicao.getCodigo());

            if (cupomRepositorio.existePorCodigoEExcluidoFalso(codigoNormalizado)) {
                throw new CodigoCupomDuplicadoException(
                    "Já existe um cupom ativo com o código: " + codigoNormalizado
                );
            }

            Cupom.validarDataExpiracao(requisicao.getDataExpiracao());
            Cupom.validarValorDesconto(requisicao.getValorDesconto());

            Cupom cupom = Cupom.builder()
                    .codigo(codigoNormalizado)
                    .descricao(requisicao.getDescricao())
                    .valorDesconto(requisicao.getValorDesconto())
                    .dataExpiracao(requisicao.getDataExpiracao())
                    .publicado(requisicao.getPublicado() != null ? requisicao.getPublicado() : false)
                    .excluido(false)
                    .build();

            cupom = cupomRepositorio.save(cupom);
            log.info("Cupom criado com sucesso. ID: {}, Código: {}", cupom.getId(), cupom.getCodigo());

            return converterParaResposta(cupom);

        } catch (IllegalArgumentException e) {
            log.error("Erro ao criar cupom: {}", e.getMessage());
            throw new CupomInvalidoException(e.getMessage());
        }
    }

    /**
     * Busca todos os cupons ativos
     */
    @Transactional(readOnly = true)
    public List<CupomResposta> obterTodosCuponsAtivos() {
        log.info("Buscando todos os cupons ativos");
        return cupomRepositorio.buscarTodosAtivos().stream()
                .map(this::converterParaResposta)
                .collect(Collectors.toList());
    }

    /**
     * Busca cupom por ID
     */
    @Transactional(readOnly = true)
    public CupomResposta obterCupomPorId(Long id) {
        log.info("Buscando cupom por ID: {}", id);
        Cupom cupom = cupomRepositorio.findById(id)
                .orElseThrow(() -> new CupomNaoEncontradoException("Cupom não encontrado com ID: " + id));

        return converterParaResposta(cupom);
    }

    /**
     * Busca cupom por código
     */
    @Transactional(readOnly = true)
    public CupomResposta obterCupomPorCodigo(String codigo) {
        log.info("Buscando cupom por código: {}", codigo);
        
        String codigoNormalizado = Cupom.normalizarCodigo(codigo);
        Cupom cupom = cupomRepositorio.buscarPorCodigoEExcluidoFalso(codigoNormalizado)
                .orElseThrow(() -> new CupomNaoEncontradoException(
                    "Cupom não encontrado com código: " + codigoNormalizado
                ));

        return converterParaResposta(cupom);
    }

    /**
     * Atualiza um cupom existente
     */
    @Transactional
    public CupomResposta atualizarCupom(Long id, CupomRequisicao requisicao) {
        log.info("Atualizando cupom ID: {}", id);

        Cupom cupom = cupomRepositorio.findById(id)
                .orElseThrow(() -> new CupomNaoEncontradoException("Cupom não encontrado com ID: " + id));

        if (cupom.getExcluido()) {
            throw new CupomInvalidoException("Não é possível atualizar um cupom deletado");
        }

        try {
            if (requisicao.getDescricao() != null) {
                cupom.setDescricao(requisicao.getDescricao());
            }

            if (requisicao.getValorDesconto() != null) {
                Cupom.validarValorDesconto(requisicao.getValorDesconto());
                cupom.setValorDesconto(requisicao.getValorDesconto());
            }

            if (requisicao.getDataExpiracao() != null) {
                Cupom.validarDataExpiracao(requisicao.getDataExpiracao());
                cupom.setDataExpiracao(requisicao.getDataExpiracao());
            }

            if (requisicao.getPublicado() != null) {
                cupom.setPublicado(requisicao.getPublicado());
            }

            cupom = cupomRepositorio.save(cupom);
            log.info("Cupom atualizado com sucesso. ID: {}", cupom.getId());

            return converterParaResposta(cupom);

        } catch (IllegalArgumentException e) {
            log.error("Erro ao atualizar cupom: {}", e.getMessage());
            throw new CupomInvalidoException(e.getMessage());
        }
    }

    /**
     * Deleta um cupom (soft delete)
     */
    @Transactional
    public void excluirCupom(Long id) {
        log.info("Deletando cupom ID: {}", id);

        Cupom cupom = cupomRepositorio.findById(id)
                .orElseThrow(() -> new CupomNaoEncontradoException("Cupom não encontrado com ID: " + id));

        try {
            cupom.excluirSoft();
            cupomRepositorio.save(cupom);
            log.info("Cupom deletado com sucesso (soft delete). ID: {}", cupom.getId());

        } catch (IllegalStateException e) {
            log.error("Erro ao deletar cupom: {}", e.getMessage());
            throw new CupomJaExcluidoException(e.getMessage());
        }
    }

    /**
     * Publica um cupom
     */
    @Transactional
    public CupomResposta publicarCupom(Long id) {
        log.info("Publicando cupom ID: {}", id);

        Cupom cupom = cupomRepositorio.findById(id)
                .orElseThrow(() -> new CupomNaoEncontradoException("Cupom não encontrado com ID: " + id));

        if (cupom.getExcluido()) {
            throw new CupomInvalidoException("Não é possível publicar um cupom deletado");
        }

        cupom.publicar();
        cupom = cupomRepositorio.save(cupom);
        log.info("Cupom publicado com sucesso. ID: {}", cupom.getId());

        return converterParaResposta(cupom);
    }

    /**
     * Despublica um cupom
     */
    @Transactional
    public CupomResposta despublicarCupom(Long id) {
        log.info("Despublicando cupom ID: {}", id);

        Cupom cupom = cupomRepositorio.findById(id)
                .orElseThrow(() -> new CupomNaoEncontradoException("Cupom não encontrado com ID: " + id));

        cupom.despublicar();
        cupom = cupomRepositorio.save(cupom);
        log.info("Cupom despublicado com sucesso. ID: {}", cupom.getId());

        return converterParaResposta(cupom);
    }

    private CupomResposta converterParaResposta(Cupom cupom) {
        return CupomResposta.builder()
                .id(cupom.getId())
                .codigo(cupom.getCodigo())
                .descricao(cupom.getDescricao())
                .valorDesconto(cupom.getValorDesconto())
                .dataExpiracao(cupom.getDataExpiracao())
                .publicado(cupom.getPublicado())
                .excluido(cupom.getExcluido())
                .ativo(cupom.estaAtivo())
                .expirado(cupom.estaExpirado())
                .criadoEm(cupom.getCriadoEm())
                .atualizadoEm(cupom.getAtualizadoEm())
                .excluidoEm(cupom.getExcluidoEm())
                .build();
    }
}
