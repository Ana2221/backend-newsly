package br.nite.newsly.repository;

import br.nite.newsly.entity.Solicitacao;
import br.nite.newsly.enums.StatusSolicitacao;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SolicitacaoRepository extends JpaRepository<Solicitacao, Long> {

    List<Solicitacao> findAllByResponsavelId(Long responsavelId);

    List<Solicitacao> findAllByGestorId(Long gestorId);

    List<Solicitacao> findAllByGestorIdAndStatus(Long gestorId, StatusSolicitacao status);

    List<Solicitacao> findAllByStatus(StatusSolicitacao status);

    List<Solicitacao> findAllByResponsavelIdAndStatus(Long responsavelId, StatusSolicitacao status);

    boolean existsByNoticiaIdAndGestorIdAndStatus(Long noticiaId, Long gestorId, StatusSolicitacao status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Solicitacao s where s.id = :id")
    Optional<Solicitacao> findByIdForUpdate(@Param("id") Long id);
}
