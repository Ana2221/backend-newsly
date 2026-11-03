package br.nite.newsly.security;

import br.nite.newsly.repository.ColaboradorRepository;
import br.nite.newsly.repository.ComentarioRepository;
import br.nite.newsly.repository.DiretorRepository;
import br.nite.newsly.repository.DiretoriaRepository;
import br.nite.newsly.repository.EmpresaJrRepository;
import br.nite.newsly.repository.FuncaoRepository;
import br.nite.newsly.repository.NoticiaRepository;
import br.nite.newsly.repository.ResponsavelRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SecurityAccessTest {

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void responsibleCanOnlyUseItsOwnCompanyId() {
        ResponsavelRepository responsavelRepository = mock(ResponsavelRepository.class);
        EmpresaJrRepository empresaRepository = mock(EmpresaJrRepository.class);
        SecurityAccess access = createAccess(responsavelRepository, empresaRepository);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "7", "token", List.of(new SimpleGrantedAuthority("ROLE_RESPONSAVEL"))));
        when(responsavelRepository.findEmpresaIdById(7L)).thenReturn(Optional.of(42L));
        when(empresaRepository.existsByIdAndResponsavelId(42L, 7L)).thenReturn(true);
        when(empresaRepository.existsByIdAndResponsavelId(43L, 7L)).thenReturn(false);

        assertEquals(42L, access.currentCompanyId());
        assertTrue(access.isOwnEmpresa(42L));
        assertFalse(access.isOwnEmpresa(43L));
        assertThrows(AccessDeniedException.class, () -> access.requireOwnCompany(43L));
    }

    @Test
    void responsableSemEmpresaAssociadaNaoRecebeAcesso() {
        ResponsavelRepository responsavelRepository = mock(ResponsavelRepository.class);
        SecurityAccess access = createAccess(responsavelRepository, mock(EmpresaJrRepository.class));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "7", "token", List.of(new SimpleGrantedAuthority("ROLE_RESPONSAVEL"))));
        when(responsavelRepository.findEmpresaIdById(7L)).thenReturn(Optional.empty());

        assertThrows(AccessDeniedException.class, access::currentCompanyId);
    }

    @Test
    void responsavelPodeUsarColaboradorDaPropriaEmpresaOuSemEmpresa() {
        ResponsavelRepository responsavelRepository = mock(ResponsavelRepository.class);
        ColaboradorRepository colaboradorRepository = mock(ColaboradorRepository.class);
        SecurityAccess access = createAccess(
                responsavelRepository, mock(EmpresaJrRepository.class), colaboradorRepository);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "7", "token", List.of(new SimpleGrantedAuthority("ROLE_RESPONSAVEL"))));
        when(responsavelRepository.findEmpresaIdById(7L)).thenReturn(Optional.of(42L));
        when(colaboradorRepository.existsByIdAndEmpresaId(10L, 42L)).thenReturn(true);
        when(colaboradorRepository.existsByIdAndEmpresaIsNull(11L)).thenReturn(true);

        assertTrue(access.isOwnOrUnassignedColaborador(10L));
        assertTrue(access.isOwnOrUnassignedColaborador(11L));
        assertFalse(access.isOwnOrUnassignedColaborador(12L));
        verify(colaboradorRepository, never()).existsByIdAndEmpresaIsNull(10L);
    }

    private SecurityAccess createAccess(
            ResponsavelRepository responsavelRepository,
            EmpresaJrRepository empresaRepository) {
        return createAccess(responsavelRepository, empresaRepository, mock(ColaboradorRepository.class));
    }

    private SecurityAccess createAccess(
            ResponsavelRepository responsavelRepository,
            EmpresaJrRepository empresaRepository,
            ColaboradorRepository colaboradorRepository) {
        return new SecurityAccess(
                responsavelRepository,
                empresaRepository,
                colaboradorRepository,
                mock(DiretoriaRepository.class),
                mock(DiretorRepository.class),
                mock(NoticiaRepository.class),
                mock(ComentarioRepository.class),
                mock(FuncaoRepository.class));
    }
}
