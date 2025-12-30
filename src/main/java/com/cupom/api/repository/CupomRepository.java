package com.cupom.api.repository;

import com.cupom.api.entity.Cupom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório para acesso a dados de Cupons
 */
@Repository
public interface CupomRepository extends JpaRepository<Cupom, Long> {

    /**
     * Busca cupom por código
     */
    Optional<Cupom> findByCode(String code);

    /**
     * Busca cupom por código ignorando deletados
     */
    @Query("SELECT c FROM Cupom c WHERE c.code = :code AND c.deleted = false")
    Optional<Cupom> findByCodeAndNotDeleted(String code);

    /**
     * Busca todos os cupons ativos (não deletados)
     */
    @Query("SELECT c FROM Cupom c WHERE c.deleted = false")
    List<Cupom> findAllActive();

    /**
     * Busca todos os cupons publicados e ativos
     */
    @Query("SELECT c FROM Cupom c WHERE c.published = true AND c.deleted = false")
    List<Cupom> findAllPublishedAndActive();

    /**
     * Verifica se existe cupom com o código (incluindo deletados)
     */
    boolean existsByCode(String code);

    /**
     * Verifica se existe cupom com o código (excluindo deletados)
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Cupom c WHERE c.code = :code AND c.deleted = false")
    boolean existsByCodeAndNotDeleted(String code);
}
