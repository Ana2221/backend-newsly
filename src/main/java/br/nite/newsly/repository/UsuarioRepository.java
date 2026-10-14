package br.nite.newsly.repository;

import br.nite.newsly.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByContatoEmail(String email);

    boolean existsByContatoEmail(String email);

    /**
     * Retorna todos os usuários cujo nome (case-insensitive) aparece em mais
     * de um registro, ordenados por nome e por id crescente.
     *
     * A ordenação garante que, ao iterar por grupo, o primeiro elemento
     * (menor id) é o mais antigo — que deve ser mantido — e os demais
     * (ids maiores) são os mais recentes — que devem ser removidos.
     *
     * A subquery usa nativeQuery para contornar o sql_mode=only_full_group_by
     * do MySQL: agrupa por LOWER(nome) e conta por id (coluna totalmente
     * dependente da chave primária), o que é aceito pelo modo estrito.
     */
    @Query(value = "SELECT u.* FROM usuario u " +
                   "WHERE LOWER(u.nome) IN (" +
                   "  SELECT LOWER(nome) FROM usuario " +
                   "  GROUP BY LOWER(nome) HAVING COUNT(id) > 1" +
                   ") ORDER BY LOWER(u.nome), u.id ASC",
           nativeQuery = true)
    List<Usuario> findDuplicatesByNome();
}
