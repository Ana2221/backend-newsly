package br.nite.newsly.repository;

import br.nite.newsly.entity.Gestor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GestorRepository extends JpaRepository<Gestor, Long> {

    Optional<Gestor> findByMatricula(UUID matricula);

    boolean existsByMatricula(UUID matricula);
}
