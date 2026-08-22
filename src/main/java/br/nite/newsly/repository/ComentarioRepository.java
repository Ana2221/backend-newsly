package br.nite.newsly.repository;

import br.nite.newsly.entity.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {

    List<Comentario> findAllByNoticiaId(Long noticiaId);

    // Busca apenas comentários raiz (sem pai) de uma notícia
    List<Comentario> findAllByNoticiaIdAndComentarioPaiIsNull(Long noticiaId);

    // Busca respostas diretas de um comentário
    List<Comentario> findAllByComentarioPaiId(Long comentarioPaiId);
}
