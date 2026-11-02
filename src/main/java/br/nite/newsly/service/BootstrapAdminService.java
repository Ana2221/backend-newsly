package br.nite.newsly.service;

import br.nite.newsly.entity.Contato;
import br.nite.newsly.entity.Usuario;
import br.nite.newsly.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class BootstrapAdminService implements ApplicationRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${security.bootstrap-admin.email:}")
    private String email;

    @Value("${security.bootstrap-admin.password:}")
    private String password;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (email == null || email.isBlank() || password == null || password.length() < 12) {
            throw new IllegalStateException(
                    "Configure NEWSLY_BOOTSTRAP_ADMIN_EMAIL e uma NEWSLY_BOOTSTRAP_ADMIN_PASSWORD com no mínimo 12 caracteres.");
        }

        Usuario admin = usuarioRepository.findByContatoEmail(email).orElseGet(() -> {
            Usuario novo = new Usuario();
            novo.setNome("Administrador Newsly");
            Contato contato = new Contato();
            contato.setEmail(email);
            novo.setContato(contato);
            return novo;
        });

        admin.setAdministrador(true);
        String storedPassword = admin.getSenha();
        if (storedPassword == null || !storedPassword.startsWith("{pbkdf2}")
                || !passwordEncoder.matches(password, storedPassword)) {
            admin.setSenha(passwordEncoder.encode(password));
        }
        usuarioRepository.save(admin);
    }
}
