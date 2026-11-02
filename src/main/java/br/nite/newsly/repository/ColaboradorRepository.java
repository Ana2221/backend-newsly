package br.nite.newsly.repository;

import br.nite.newsly.entity.Colaborador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ColaboradorRepository extends JpaRepository<Colaborador, Long> {

    Optional<Colaborador> findByMatricula(UUID matricula);

    List<Colaborador> findAllByEmpresaId(Long empresaId);

    boolean existsByMatricula(UUID matricula);

    boolean existsByIdAndEmpresaId(Long id, Long empresaId);
}
