package br.nite.newsly.repository;

import br.nite.newsly.entity.EmpresaJr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmpresaJrRepository extends JpaRepository<EmpresaJr, Long> {

    Optional<EmpresaJr> findByCnpj(String cnpj);

    boolean existsByCnpj(String cnpj);

    boolean existsByContatoEmailIgnoreCase(String email);

    boolean existsByContatoEmailIgnoreCaseAndIdNot(String email, Long id);

    boolean existsByIdAndResponsavelId(Long id, Long responsavelId);
}
