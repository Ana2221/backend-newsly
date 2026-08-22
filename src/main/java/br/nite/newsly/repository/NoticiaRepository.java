package br.nite.newsly.repository;

import br.nite.newsly.entity.Noticia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticiaRepository extends JpaRepository<Noticia, Long> {

    List<Noticia> findAllByEmpresaId(Long empresaId);
}
