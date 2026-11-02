package br.nite.newsly.service;

import br.nite.newsly.dto.request.ContatoRequestDTO;
import br.nite.newsly.dto.request.EmpresaJrRequestDTO;
import br.nite.newsly.entity.Contato;
import br.nite.newsly.entity.EmpresaJr;
import br.nite.newsly.entity.Responsavel;
import br.nite.newsly.exception.BusinessException;
import br.nite.newsly.mapper.EmpresaJrMapper;
import br.nite.newsly.repository.ColaboradorRepository;
import br.nite.newsly.repository.EmpresaJrRepository;
import br.nite.newsly.repository.ResponsavelRepository;
import br.nite.newsly.security.SecurityAccess;
import br.nite.newsly.service.storage.ArmazenamentoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmpresaJrServiceTest {

    private static final Long RESPONSAVEL_ID = 7L;
    private static final String CNPJ = "11.222.333/0001-81";
    private static final String EMAIL = "contato@minhaej.com";

    @Mock
    private EmpresaJrRepository empresaRepository;
    @Mock
    private ResponsavelRepository responsavelRepository;
    @Mock
    private ColaboradorRepository colaboradorRepository;
    @Mock
    private EmpresaJrMapper mapper;
    @Mock
    private ArmazenamentoService armazenamentoService;
    @Mock
    private SecurityAccess securityAccess;
    @Mock
    private CnpjValidationService cnpjValidationService;

    private EmpresaJrService service;

    @BeforeEach
    void setUp() {
        service = new EmpresaJrService(
                empresaRepository,
                responsavelRepository,
                colaboradorRepository,
                mapper,
                armazenamentoService,
                securityAccess,
                cnpjValidationService);
    }

    @Test
    void responsavelCriaEmpresaAssociadaAoProprioUsuario() {
        Responsavel responsavel = new Responsavel();
        EmpresaJr empresa = new EmpresaJr();
        empresa.setContato(new Contato());

        when(securityAccess.isCurrentResponsible()).thenReturn(true);
        when(securityAccess.currentUserId()).thenReturn(RESPONSAVEL_ID);
        when(responsavelRepository.findByIdForUpdate(RESPONSAVEL_ID)).thenReturn(Optional.of(responsavel));
        when(empresaRepository.existsByCnpj(CNPJ)).thenReturn(false);
        when(empresaRepository.existsByContatoEmailIgnoreCase(EMAIL)).thenReturn(false);
        when(mapper.toEntity(any(EmpresaJrRequestDTO.class))).thenReturn(empresa);
        when(empresaRepository.save(empresa)).thenReturn(empresa);

        service.criar(request(999L));

        verify(cnpjValidationService).validar(CNPJ);
        verify(empresaRepository).save(empresa);
        verify(responsavelRepository).save(responsavel);
        org.junit.jupiter.api.Assertions.assertSame(empresa, responsavel.getEmpresa());
        org.junit.jupiter.api.Assertions.assertEquals(EMAIL, empresa.getContato().getEmail());
    }

    @Test
    void impedeResponsavelDeCriarUmaSegundaEmpresa() {
        Responsavel responsavel = new Responsavel();
        responsavel.setEmpresa(new EmpresaJr());
        when(securityAccess.isCurrentResponsible()).thenReturn(true);
        when(securityAccess.currentUserId()).thenReturn(RESPONSAVEL_ID);
        when(responsavelRepository.findByIdForUpdate(RESPONSAVEL_ID)).thenReturn(Optional.of(responsavel));

        assertThrows(BusinessException.class, () -> service.criar(request(RESPONSAVEL_ID)));

        verifyNoInteractions(empresaRepository, cnpjValidationService);
        verify(responsavelRepository, never()).save(any());
    }

    @Test
    void impedeEmailAssociadoAOutraEmpresa() {
        Responsavel responsavel = new Responsavel();
        when(securityAccess.isCurrentResponsible()).thenReturn(true);
        when(securityAccess.currentUserId()).thenReturn(RESPONSAVEL_ID);
        when(responsavelRepository.findByIdForUpdate(RESPONSAVEL_ID)).thenReturn(Optional.of(responsavel));
        when(empresaRepository.existsByCnpj(CNPJ)).thenReturn(false);
        when(empresaRepository.existsByContatoEmailIgnoreCase(EMAIL)).thenReturn(true);

        assertThrows(BusinessException.class, () -> service.criar(request(RESPONSAVEL_ID)));

        verifyNoInteractions(cnpjValidationService);
        verify(empresaRepository, never()).save(any());
    }

    private EmpresaJrRequestDTO request(Long responsavelId) {
        return new EmpresaJrRequestDTO(
                "Minha EJ",
                CNPJ,
                new ContatoRequestDTO(null, EMAIL),
                null,
                null,
                responsavelId);
    }
}
