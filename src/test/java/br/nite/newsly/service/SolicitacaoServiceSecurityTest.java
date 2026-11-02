package br.nite.newsly.service;

import br.nite.newsly.dto.request.SolicitacaoRequestDTO;
import br.nite.newsly.entity.EmpresaJr;
import br.nite.newsly.entity.Gestor;
import br.nite.newsly.entity.Noticia;
import br.nite.newsly.entity.Responsavel;
import br.nite.newsly.entity.Solicitacao;
import br.nite.newsly.enums.StatusSolicitacao;
import br.nite.newsly.repository.SolicitacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SolicitacaoServiceSecurityTest {

    private SolicitacaoRepository repository;
    private ResponsavelService responsavelService;
    private GestorService gestorService;
    private NoticiaService noticiaService;
    private SolicitacaoService service;
    private Solicitacao solicitacao;

    @BeforeEach
    void setUp() {
        repository = mock(SolicitacaoRepository.class);
        responsavelService = mock(ResponsavelService.class);
        gestorService = mock(GestorService.class);
        noticiaService = mock(NoticiaService.class);
        service = new SolicitacaoService(
                repository, responsavelService, gestorService, noticiaService);

        Responsavel responsavel = new Responsavel();
        responsavel.setId(8L);
        responsavel.setNome("Maria");
        Gestor gestor = new Gestor();
        gestor.setId(5L);
        gestor.setNome("Carlos");
        EmpresaJr empresa = new EmpresaJr();
        empresa.setId(7L);
        empresa.setNome("Nite Jr");
        Noticia noticia = new Noticia();
        noticia.setId(3L);
        noticia.setTitulo("Notícia");
        noticia.setEmpresa(empresa);

        solicitacao = new Solicitacao();
        solicitacao.setId(12L);
        solicitacao.setResponsavel(responsavel);
        solicitacao.setGestor(gestor);
        solicitacao.setNoticia(noticia);
        solicitacao.setStatus(StatusSolicitacao.PENDENTE);
        when(repository.findById(12L)).thenReturn(Optional.of(solicitacao));
        when(repository.findByIdForUpdate(12L)).thenReturn(Optional.of(solicitacao));
        when(repository.save(any(Solicitacao.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(responsavelService.buscarEntidade(8L)).thenReturn(responsavel);
        when(gestorService.buscarEntidade(5L)).thenReturn(gestor);
        when(noticiaService.buscarEmpresaParaSubmissao(7L)).thenReturn(empresa);
    }

    @Test
    void submissaoCriaPendenciaSemCriarNoticia() {
        var response = service.enviar(new SolicitacaoRequestDTO(
                8L, "Nova parceria", "Conteúdo da notícia", java.time.LocalDateTime.now(), 7L));

        assertEquals(StatusSolicitacao.PENDENTE, response.status());
        assertNull(response.noticiaId());
        assertNull(response.gestorId());
        assertEquals("Nova parceria", response.noticiaTitulo());
        assertEquals("Conteúdo da notícia", response.noticiaConteudo());
        verify(repository).save(any(Solicitacao.class));
        verifyNoInteractions(gestorService);
        verify(noticiaService, never()).criarAprovada(any());
    }

    @Test
    void aprovarSubmissaoCriaNoticiaSomenteNaAprovacao() {
        solicitacao.setNoticia(null);
        solicitacao.setEmpresa(new EmpresaJr());
        solicitacao.getEmpresa().setId(7L);
        solicitacao.setTituloSubmetido("Submetida");
        solicitacao.setConteudoSubmetido("Conteúdo submetido");
        solicitacao.setPublicadaEmSubmetida(java.time.LocalDateTime.now());
        Noticia noticiaCriada = new Noticia();
        noticiaCriada.setId(30L);
        noticiaCriada.setTitulo("Submetida");
        when(noticiaService.criarAprovada(any())).thenReturn(noticiaCriada);
        Gestor gestorQueAprovou = new Gestor();
        gestorQueAprovou.setId(6L);
        gestorQueAprovou.setNome("Gestor aprovador");
        when(gestorService.buscarEntidade(6L)).thenReturn(gestorQueAprovou);

        var response = service.aceitar(12L, "6", false);

        assertEquals(30L, response.noticiaId());
        assertEquals(6L, response.gestorId());
        assertEquals(StatusSolicitacao.ACEITA, response.status());
        verify(noticiaService).criarAprovada(any());
    }

    @Test
    void gestorPodeAceitarSolicitacaoAtribuidaAOutroGestor() {
        service.aceitar(12L, "6", false);

        assertEquals(StatusSolicitacao.ACEITA, solicitacao.getStatus());
        verify(noticiaService).aprovarPublicacao(3L);
        verify(repository).save(solicitacao);
    }

    @Test
    void recusarSolicitacaoNaoPublicaNoticia() {
        service.recusar(12L, null, "5", false);

        assertEquals(StatusSolicitacao.RECUSADA, solicitacao.getStatus());
        verify(noticiaService, never()).aprovarPublicacao(anyLong());
    }

    @Test
    void responsavelNaoPodeCancelarSolicitacaoDeOutroResponsavel() {
        assertThrows(AccessDeniedException.class, () -> service.cancelar(12L, "9", false));
        verify(repository, never()).save(any(Solicitacao.class));
    }

    @Test
    void usuarioNaoAssociadoNaoPodeConsultarSolicitacao() {
        assertThrows(AccessDeniedException.class, () -> service.buscarPorId(12L, "9", false, false));
    }

    @Test
    void filaDePendenciasIncluiSubmissoesDeTodosOsGestores() {
        when(repository.findAllByStatus(StatusSolicitacao.PENDENTE)).thenReturn(List.of(solicitacao));

        assertEquals(1, service.buscarPendentesParaGestor().size());

        verify(repository).findAllByStatus(StatusSolicitacao.PENDENTE);
    }
}
