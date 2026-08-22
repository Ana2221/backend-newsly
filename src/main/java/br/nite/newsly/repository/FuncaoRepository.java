package br.nite.newsly.repository;

import br.nite.newsly.entity.Funcao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FuncaoRepository extends JpaRepository<Funcao, Long> {

    Optional<Funcao> findByNome(String nome);

    boolean existsByNome(String nome);
}
