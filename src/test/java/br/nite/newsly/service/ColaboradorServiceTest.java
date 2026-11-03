package br.nite.newsly.service;

import br.nite.newsly.dto.response.ColaboradorResponseDTO;
import br.nite.newsly.entity.Colaborador;
import br.nite.newsly.mapper.ColaboradorMapper;
import br.nite.newsly.repository.ColaboradorRepository;
import br.nite.newsly.security.SecurityAccess;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ColaboradorServiceTest {

    @Test
    void responsavelListaColaboradoresDaPropriaEmpresaEColaboradoresSemEmpresa() {
        ColaboradorRepository repository = mock(ColaboradorRepository.class);
        ColaboradorMapper mapper = mock(ColaboradorMapper.class);
        SecurityAccess securityAccess = mock(SecurityAccess.class);
        Colaborador colaborador = new Colaborador();
        colaborador.setId(13L);
        ColaboradorResponseDTO response = new ColaboradorResponseDTO(
                13L, null, "Colaborador", true, null, null, null);
        when(securityAccess.isCurrentResponsible()).thenReturn(true);
        when(securityAccess.currentCompanyId()).thenReturn(1L);
        when(repository.findAllByEmpresaIdOrEmpresaIsNull(1L)).thenReturn(List.of(colaborador));
        when(mapper.toResponse(colaborador)).thenReturn(response);

        ColaboradorService service = new ColaboradorService(
                repository,
                mapper,
                mock(EmpresaJrService.class),
                mock(PasswordEncoder.class),
                securityAccess);

        assertEquals(List.of(response), service.buscarTodos());
        verify(repository).findAllByEmpresaIdOrEmpresaIsNull(1L);
        verify(repository, never()).findAll();
    }
}
