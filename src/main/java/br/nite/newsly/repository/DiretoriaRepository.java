package br.nite.newsly.repository;

import br.nite.newsly.entity.Diretoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiretoriaRepository extends JpaRepository<Diretoria, Long> {

    List<Diretoria> findAllByEmpresaId(Long empresaId);
}
