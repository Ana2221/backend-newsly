package br.nite.newsly.repository;

import br.nite.newsly.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByContatoEmail(String email);

    boolean existsByContatoEmail(String email);
}
