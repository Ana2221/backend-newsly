package br.nite.newsly.repository;

import br.nite.newsly.entity.Noticia;
import br.nite.newsly.enums.StatusSolicitacao;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoticiaRepository extends JpaRepository<Noticia, Long> {

    List<Noticia> findAllByEmpresaId(Long empresaId);

    @Query("select n from Noticia n where n.aprovada = true and "
            + "(not exists (select s.id from Solicitacao s where s.noticia = n) or exists "
            + "(select s.id from Solicitacao s where s.noticia = n and s.status = :aceita))")
    List<Noticia> findAllPublicadas(@Param("aceita") StatusSolicitacao aceita);

    @Query("select distinct n from Noticia n where n.aprovada = true or exists "
            + "(select s.id from Solicitacao s where s.noticia = n and s.status = :status)")
    List<Noticia> findAllAprovadasOuPendentes(@Param("status") StatusSolicitacao status);

    @Query("select n from Noticia n where n.id = :id and n.aprovada = true and "
            + "(not exists (select s.id from Solicitacao s where s.noticia = n) or exists "
            + "(select s.id from Solicitacao s where s.noticia = n and s.status = :aceita))")
    Optional<Noticia> findPublicadaById(
            @Param("id") Long id, @Param("aceita") StatusSolicitacao aceita);

    @Query("select n from Noticia n where n.id = :id and (n.aprovada = true or exists "
            + "(select s.id from Solicitacao s where s.noticia = n and s.status = :status))")
    Optional<Noticia> findAprovadaOuPendenteParaGestor(
            @Param("id") Long id, @Param("status") StatusSolicitacao status);

    boolean existsByIdAndEmpresaId(Long id, Long empresaId);
}
