package br.nite.newsly.service;

import br.nite.newsly.dto.request.CriarUsuarioRequestDTO;
import br.nite.newsly.dto.request.ColaboradorRequestDTO;
import br.nite.newsly.dto.request.ResponsavelRequestDTO;
import br.nite.newsly.dto.response.ColaboradorResponseDTO;
import br.nite.newsly.dto.response.ResponsavelResponseDTO;
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
import static org.mockito.ArgumentMatchers.argThat;
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
    void cadastroPublicoPermiteColaboradorSemAssociarEmpresa() {
        UsuarioService usuarioService = mock(UsuarioService.class);
        ColaboradorService colaboradorService = mock(ColaboradorService.class);
        GestorService gestorService = mock(GestorService.class);
        ResponsavelService responsavelService = mock(ResponsavelService.class);
        UsuarioAdapterService service = new UsuarioAdapterService(
                usuarioService, colaboradorService, gestorService, responsavelService);
        CriarUsuarioRequestDTO request = new CriarUsuarioRequestDTO(
                TipoUsuario.COLABORADOR, "Ana", "senha-segura-123", null, true, null, null);
        when(colaboradorService.criar(any())).thenReturn(
                new ColaboradorResponseDTO(1L, null, "Ana", true, null, null, TipoUsuario.COLABORADOR));

        service.criar(request);

        verify(colaboradorService).criar(argThat(dto ->
                dto.empresaId() == null && Boolean.TRUE.equals(dto.atividade())));
        verifyNoInteractions(usuarioService, gestorService, responsavelService);
    }

    @Test
    void cadastroPublicoDeColaboradorNaoPodeEscolherEmpresa() {
        UsuarioService usuarioService = mock(UsuarioService.class);
        ColaboradorService colaboradorService = mock(ColaboradorService.class);
        GestorService gestorService = mock(GestorService.class);
        ResponsavelService responsavelService = mock(ResponsavelService.class);
        UsuarioAdapterService service = new UsuarioAdapterService(
                usuarioService, colaboradorService, gestorService, responsavelService);
        CriarUsuarioRequestDTO request = new CriarUsuarioRequestDTO(
                TipoUsuario.COLABORADOR, "Ana", "senha-segura-123", null, true, 42L, null);

        assertThrows(AccessDeniedException.class, () -> service.criar(request));
        verifyNoInteractions(usuarioService, colaboradorService, gestorService, responsavelService);
    }

    @Test
    void cadastroPublicoPermiteResponsavelSemEmpresaOuPermissoes() {
        UsuarioService usuarioService = mock(UsuarioService.class);
        ColaboradorService colaboradorService = mock(ColaboradorService.class);
        GestorService gestorService = mock(GestorService.class);
        ResponsavelService responsavelService = mock(ResponsavelService.class);
        UsuarioAdapterService service = new UsuarioAdapterService(
                usuarioService, colaboradorService, gestorService, responsavelService);
        CriarUsuarioRequestDTO request = new CriarUsuarioRequestDTO(
                TipoUsuario.RESPONSAVEL, "Maria", "senha-segura-123", null, null, null, null);
        when(responsavelService.criar(any())).thenReturn(
                new ResponsavelResponseDTO(1L, null, "Maria", null, null, null, TipoUsuario.RESPONSAVEL));

        service.criar(request);

        verify(responsavelService).criar(argThat(dto ->
                dto.empresaId() == null && dto.permissoes() == null));
        verifyNoInteractions(usuarioService, colaboradorService, gestorService);
    }

    @Test
    void cadastroPublicoDeResponsavelNaoPodeEscolherEmpresaOuPermissoes() {
        UsuarioService usuarioService = mock(UsuarioService.class);
        ColaboradorService colaboradorService = mock(ColaboradorService.class);
        GestorService gestorService = mock(GestorService.class);
        ResponsavelService responsavelService = mock(ResponsavelService.class);
        UsuarioAdapterService service = new UsuarioAdapterService(
                usuarioService, colaboradorService, gestorService, responsavelService);
        CriarUsuarioRequestDTO request = new CriarUsuarioRequestDTO(
                TipoUsuario.RESPONSAVEL, "Maria", "senha-segura-123", null, null, 42L, null);

        assertThrows(AccessDeniedException.class, () -> service.criar(request));
        verifyNoInteractions(usuarioService, colaboradorService, gestorService, responsavelService);
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
