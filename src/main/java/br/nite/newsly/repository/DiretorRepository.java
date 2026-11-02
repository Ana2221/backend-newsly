package br.nite.newsly.repository;

import br.nite.newsly.entity.Diretor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiretorRepository extends JpaRepository<Diretor, Long> {

    boolean existsByColaboradorId(Long colaboradorId);

    List<Diretor> findAllByDiretoriaId(Long diretoriaId);

    Optional<Diretor> findByColaboradorId(Long colaboradorId);

    List<Diretor> findAllByDiretoriaEmpresaId(Long empresaId);

    boolean existsByIdAndDiretoriaEmpresaId(Long id, Long empresaId);
}
