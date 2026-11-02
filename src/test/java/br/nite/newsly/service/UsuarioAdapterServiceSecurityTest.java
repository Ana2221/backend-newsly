package br.nite.newsly.service;

import br.nite.newsly.dto.request.CriarUsuarioRequestDTO;
import br.nite.newsly.dto.response.UsuarioResponseDTO;
import br.nite.newsly.enums.TipoUsuario;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UsuarioAdapterServiceSecurityTest {

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void cadastroAnonimoNaoPodeCriarGestor() {
        UsuarioService usuarioService = mock(UsuarioService.class);
        ColaboradorService colaboradorService = mock(ColaboradorService.class);
        GestorService gestorService = mock(GestorService.class);
        ResponsavelService responsavelService = mock(ResponsavelService.class);
        UsuarioAdapterService service = new UsuarioAdapterService(
                usuarioService, colaboradorService, gestorService, responsavelService);
        CriarUsuarioRequestDTO request = new CriarUsuarioRequestDTO(
                TipoUsuario.GESTOR, "Carlos", "senha-segura-123", null, null, null, List.of());

        assertThrows(AccessDeniedException.class, () -> service.criar(request));
        verifyNoInteractions(usuarioService, colaboradorService, gestorService, responsavelService);
    }

    @Test
    void cadastroPublicoPermiteSomenteUsuarioBasico() {
        UsuarioService usuarioService = mock(UsuarioService.class);
        ColaboradorService colaboradorService = mock(ColaboradorService.class);
        GestorService gestorService = mock(GestorService.class);
        ResponsavelService responsavelService = mock(ResponsavelService.class);
        UsuarioAdapterService service = new UsuarioAdapterService(
                usuarioService, colaboradorService, gestorService, responsavelService);
        CriarUsuarioRequestDTO request = new CriarUsuarioRequestDTO(
                TipoUsuario.USUARIO, "Ana", "senha-segura-123", null, null, null, null);
        when(usuarioService.criar(any())).thenReturn(new UsuarioResponseDTO(1L, "Ana", null));

        service.criar(request);

        verify(usuarioService).criar(any());
        verifyNoInteractions(colaboradorService, gestorService, responsavelService);
    }

    @Test
    void administradorPodeCriarGestor() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "1", "token", List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))));
        UsuarioService usuarioService = mock(UsuarioService.class);
        ColaboradorService colaboradorService = mock(ColaboradorService.class);
        GestorService gestorService = mock(GestorService.class);
        ResponsavelService responsavelService = mock(ResponsavelService.class);
        UsuarioAdapterService service = new UsuarioAdapterService(
                usuarioService, colaboradorService, gestorService, responsavelService);
        CriarUsuarioRequestDTO request = new CriarUsuarioRequestDTO(
                TipoUsuario.GESTOR, "Carlos", "senha-segura-123", null, null, null, List.of());

        service.criar(request);

        verify(gestorService).criar(any());
    }
}
