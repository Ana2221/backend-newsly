package br.nite.newsly.service;

import br.nite.newsly.configuration.SecurityConfiguration;
import br.nite.newsly.dto.request.LoginRequestDTO;
import br.nite.newsly.dto.request.UsuarioRequestDTO;
import br.nite.newsly.dto.response.UsuarioResponseDTO;
import br.nite.newsly.entity.Usuario;
import br.nite.newsly.mapper.UsuarioMapper;
import br.nite.newsly.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UsuarioServiceSecurityTest {

    private UsuarioRepository repository;
    private UsuarioMapper mapper;
    private PasswordEncoder passwordEncoder;
    private UsuarioService service;

    @BeforeEach
    void setUp() {
        repository = mock(UsuarioRepository.class);
        mapper = mock(UsuarioMapper.class);
        passwordEncoder = new SecurityConfiguration().passwordEncoder();
        service = new UsuarioService(repository, mapper, passwordEncoder);
    }

    @Test
    void criaUsuarioComSenhaEmHash() {
        UsuarioRequestDTO request = new UsuarioRequestDTO("Ana", "senha-segura-123", null);
        Usuario entity = new Usuario();
        when(mapper.toEntity(request)).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(new UsuarioResponseDTO(1L, "Ana", null));
        when(repository.save(entity)).thenReturn(entity);

        service.criar(request);

        assertNotEquals(request.senha(), entity.getSenha());
        assertTrue(passwordEncoder.matches(request.senha(), entity.getSenha()));
        verify(repository).save(entity);
    }

    @Test
    void migraSenhaLegadaAposLoginValido() {
        Usuario usuario = usuarioComSenha("senha-legada");
        when(repository.findByContatoEmail("ana@example.com")).thenReturn(Optional.of(usuario));
        when(repository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario autenticado = service.login(new LoginRequestDTO("ana@example.com", "senha-legada"));

        assertSame(usuario, autenticado);
        assertNotEquals("senha-legada", usuario.getSenha());
        assertTrue(passwordEncoder.matches("senha-legada", usuario.getSenha()));
        verify(repository).save(usuario);
    }

    @Test
    void rejeitaSenhaIncorretaSemRegravarCredenciais() {
        Usuario usuario = usuarioComSenha(passwordEncoder.encode("senha-correta"));
        when(repository.findByContatoEmail("ana@example.com")).thenReturn(Optional.of(usuario));

        assertThrows(BadCredentialsException.class,
                () -> service.login(new LoginRequestDTO("ana@example.com", "senha-incorreta")));

        verify(repository, never()).save(any(Usuario.class));
    }

    private Usuario usuarioComSenha(String senha) {
        Usuario usuario = new Usuario();
        usuario.setSenha(senha);
        return usuario;
    }
}
