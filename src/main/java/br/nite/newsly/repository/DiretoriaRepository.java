package br.nite.newsly.repository;

import br.nite.newsly.entity.Diretoria;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiretoriaRepository extends JpaRepository<Diretoria, Long> {

    @Override
    @EntityGraph(attributePaths = {"empresa", "diretores", "diretores.colaborador", "diretores.funcao"})
    Optional<Diretoria> findById(Long id);

    @Override
    @EntityGraph(attributePaths = {"empresa", "diretores", "diretores.colaborador", "diretores.funcao"})
    List<Diretoria> findAll();

    @EntityGraph(attributePaths = {"empresa", "diretores", "diretores.colaborador", "diretores.funcao"})
    List<Diretoria> findAllByEmpresaId(Long empresaId);

    boolean existsByIdAndEmpresaId(Long id, Long empresaId);
}
