package com.cupom.api.repository;

import com.cupom.api.domain.Cupom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório para acesso a dados de Cupons
 */
@Repository
public interface CupomRepositorio extends JpaRepository<Cupom, Long> {

    Optional<Cupom> findByCodigo(String codigo);

    @Query("SELECT c FROM Cupom c WHERE c.codigo = :codigo AND c.excluido = false")
    Optional<Cupom> buscarPorCodigoEExcluidoFalso(String codigo);

    @Query("SELECT c FROM Cupom c WHERE c.excluido = false")
    List<Cupom> buscarTodosAtivos();

    @Query("SELECT c FROM Cupom c WHERE c.publicado = true AND c.excluido = false")
    List<Cupom> buscarTodosPublicadosEAtivos();

    boolean existsByCodigo(String codigo);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Cupom c WHERE c.codigo = :codigo AND c.excluido = false")
    boolean existePorCodigoEExcluidoFalso(String codigo);
}
