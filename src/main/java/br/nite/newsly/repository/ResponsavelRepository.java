package br.nite.newsly.repository;

import br.nite.newsly.entity.Responsavel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResponsavelRepository extends JpaRepository<Responsavel, Long> {

    Optional<Responsavel> findByMatricula(UUID matricula);

    boolean existsByEmpresaId(Long empresaId);
}
