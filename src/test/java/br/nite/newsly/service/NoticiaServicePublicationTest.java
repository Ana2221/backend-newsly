package br.nite.newsly.service;

import br.nite.newsly.dto.request.NoticiaRequestDTO;
import br.nite.newsly.entity.EmpresaJr;
import br.nite.newsly.entity.Noticia;
import br.nite.newsly.enums.StatusSolicitacao;
import br.nite.newsly.mapper.NoticiaMapper;
import br.nite.newsly.repository.NoticiaRepository;
import br.nite.newsly.security.SecurityAccess;
import br.nite.newsly.service.storage.ArmazenamentoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class NoticiaServicePublicationTest {

    private NoticiaRepository repository;
    private NoticiaMapper mapper;
    private EmpresaJrService empresaJrService;
    private SecurityAccess securityAccess;
    private NoticiaService service;

    @BeforeEach
    void setUp() {
        repository = mock(NoticiaRepository.class);
        mapper = mock(NoticiaMapper.class);
        empresaJrService = mock(EmpresaJrService.class);
        securityAccess = mock(SecurityAccess.class);
        service = new NoticiaService(
                repository, mapper, empresaJrService, mock(ArmazenamentoService.class), securityAccess);
        when(repository.save(any(Noticia.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void noticiaCriadaPorResponsavelFicaOcultaAteAprovacao() {
        EmpresaJr empresa = new EmpresaJr();
        empresa.setId(7L);
        Noticia noticia = new Noticia();
        when(securityAccess.isCurrentResponsible()).thenReturn(true);
        when(empresaJrService.buscarEntidade(7L)).thenReturn(empresa);
        when(mapper.toEntity(any(NoticiaRequestDTO.class))).thenReturn(noticia);

        service.criar(new NoticiaRequestDTO("Título", "Conteúdo", LocalDateTime.now(), 7L));

        assertFalse(noticia.isAprovada());
        verify(repository).save(noticia);
    }

    @Test
    void aceiteDaSolicitacaoAprovaANoticia() {
        Noticia noticia = new Noticia();
        noticia.setId(12L);
        noticia.setAprovada(false);
        when(repository.findById(12L)).thenReturn(Optional.of(noticia));

        service.aprovarPublicacao(12L);

        assertTrue(noticia.isAprovada());
        verify(repository).save(noticia);
    }

    @Test
    void criarAprovadaPersisteNoticiaApenasQuandoChamadoPeloAceite() {
        EmpresaJr empresa = new EmpresaJr();
        empresa.setId(7L);
        Noticia noticia = new Noticia();
        when(empresaJrService.buscarEntidade(7L)).thenReturn(empresa);
        when(mapper.toEntity(any(NoticiaRequestDTO.class))).thenReturn(noticia);

        Noticia criada = service.criarAprovada(
                new NoticiaRequestDTO("Título", "Conteúdo", LocalDateTime.now(), 7L));

        assertTrue(criada.isAprovada());
        assertTrue(criada.getEmpresa() == empresa);
        verify(repository).save(noticia);
    }

    @Test
    void gestoresConsultamNoticiasAprovadasEOsEnviosPendentes() {
        when(securityAccess.isCurrentManager()).thenReturn(true);
        when(repository.findAllAprovadasOuPendentes(StatusSolicitacao.PENDENTE)).thenReturn(List.of());

        service.buscarTodos();

        verify(repository).findAllAprovadasOuPendentes(StatusSolicitacao.PENDENTE);
        verify(repository, never()).findAll();
    }

    @Test
    void leitorConsultaSomenteNoticiasAprovadas() {
        when(repository.findAllPublicadas(StatusSolicitacao.ACEITA)).thenReturn(List.of());

        service.buscarTodos();

        verify(repository).findAllPublicadas(StatusSolicitacao.ACEITA);
        verify(repository, never()).findAll();
    }
}
