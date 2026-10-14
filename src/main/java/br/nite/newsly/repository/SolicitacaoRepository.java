package br.nite.newsly.repository;

import br.nite.newsly.entity.Solicitacao;
import br.nite.newsly.enums.StatusSolicitacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolicitacaoRepository extends JpaRepository<Solicitacao, Long> {

    List<Solicitacao> findAllByResponsavelId(Long responsavelId);

    List<Solicitacao> findAllByGestorId(Long gestorId);

    List<Solicitacao> findAllByGestorIdAndStatus(Long gestorId, StatusSolicitacao status);

    List<Solicitacao> findAllByResponsavelIdAndStatus(Long responsavelId, StatusSolicitacao status);

    boolean existsByNoticiaIdAndGestorIdAndStatus(Long noticiaId, Long gestorId, StatusSolicitacao status);
}
