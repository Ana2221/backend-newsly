package br.nite.newsly.repository;

import br.nite.newsly.entity.Responsavel;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResponsavelRepository extends JpaRepository<Responsavel, Long> {

    Optional<Responsavel> findByMatricula(UUID matricula);

    boolean existsByEmpresaId(Long empresaId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from Responsavel r where r.id = :id")
    Optional<Responsavel> findByIdForUpdate(@Param("id") Long id);

    @Query("select r.empresa.id from Responsavel r where r.id = :id")
    Optional<Long> findEmpresaIdById(@Param("id") Long id);
}
