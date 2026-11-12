package br.nite.newsly.service;

import br.nite.newsly.dto.request.ResponderSolicitacaoRequestDTO;
import br.nite.newsly.dto.request.SolicitacaoRequestDTO;
import br.nite.newsly.dto.request.NoticiaRequestDTO;
import br.nite.newsly.dto.response.SolicitacaoResponseDTO;
import br.nite.newsly.entity.EmpresaJr;
import br.nite.newsly.entity.Gestor;
import br.nite.newsly.entity.Noticia;
import br.nite.newsly.entity.Responsavel;
import br.nite.newsly.entity.Solicitacao;
import br.nite.newsly.enums.StatusSolicitacao;
import br.nite.newsly.exception.BusinessException;
import br.nite.newsly.exception.ResourceNotFoundException;
import br.nite.newsly.repository.SolicitacaoRepository;
import br.nite.newsly.service.storage.ArmazenamentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SolicitacaoService {

    private final SolicitacaoRepository repository;
    private final ResponsavelService responsavelService;
    private final GestorService gestorService;
    private final NoticiaService noticiaService;
    private final ArmazenamentoService armazenamentoService;

    @Transactional
    public SolicitacaoResponseDTO enviar(SolicitacaoRequestDTO dto) {
        return enviar(dto, null);
    }

    @Transactional
    public SolicitacaoResponseDTO enviar(SolicitacaoRequestDTO dto, MultipartFile arquivo) {
        Responsavel responsavel = responsavelService.buscarEntidade(dto.responsavelId());
        EmpresaJr empresa = noticiaService.buscarEmpresaParaSubmissao(dto.empresaId());

        Solicitacao entity = new Solicitacao();
        entity.setResponsavel(responsavel);
        entity.setEmpresa(empresa);
        entity.setTituloSubmetido(dto.titulo());
        entity.setConteudoSubmetido(dto.conteudo());
        entity.setPublicadaEmSubmetida(dto.publicadaEm());
        if (arquivo != null && !arquivo.isEmpty()) {
            entity.setImagemSubmetida(armazenamentoService.salvar(arquivo, "solicitacoes"));
        }
        entity.setStatus(StatusSolicitacao.PENDENTE);
        entity.setCriadaEm(LocalDateTime.now());

        return toResponse(repository.save(entity));
    }

    @Transactional
    public SolicitacaoResponseDTO aceitar(Long id, String actorId, boolean administrador) {
        Solicitacao entity = buscarEntidadeParaAtualizar(id);
        validarPendente(entity);
        if (!administrador) {
            entity.setGestor(gestorService.buscarEntidade(Long.valueOf(actorId)));
        }
        if (entity.getNoticia() == null) {
            if (entity.getEmpresa() == null
                    || entity.getTituloSubmetido() == null
                    || entity.getConteudoSubmetido() == null
                    || entity.getPublicadaEmSubmetida() == null) {
                throw new BusinessException("A solicitação não contém os dados completos da notícia.");
            }
            NoticiaRequestDTO noticiaRequest = new NoticiaRequestDTO(
                    entity.getTituloSubmetido(),
                    entity.getConteudoSubmetido(),
                    entity.getPublicadaEmSubmetida(),
                    entity.getEmpresa().getId());
            Noticia noticia = noticiaService.criarAprovada(noticiaRequest);
            noticia.setImagemUrl(entity.getImagemSubmetida());
            entity.setNoticia(noticia);
        } else {
            noticiaService.aprovarPublicacao(entity.getNoticia().getId());
        }
        entity.setStatus(StatusSolicitacao.ACEITA);
        entity.setRespondidaEm(LocalDateTime.now());
        return toResponse(repository.save(entity));
    }

    @Transactional
    public SolicitacaoResponseDTO recusar(
            Long id, ResponderSolicitacaoRequestDTO dto, String actorId, boolean administrador) {
        Solicitacao entity = buscarEntidadeParaAtualizar(id);
        validarPendente(entity);
        if (!administrador) {
            entity.setGestor(gestorService.buscarEntidade(Long.valueOf(actorId)));
        }
        entity.setStatus(StatusSolicitacao.RECUSADA);
        entity.setRespondidaEm(LocalDateTime.now());
        entity.setMotivoRecusa(dto != null ? dto.motivoRecusa() : null);
        return toResponse(repository.save(entity));
    }

    @Transactional
    public SolicitacaoResponseDTO cancelar(Long id, String actorId, boolean administrador) {
        Solicitacao entity = buscarEntidadeParaAtualizar(id);
        validarResponsavel(entity, actorId, administrador);
        validarPendente(entity);
        entity.setStatus(StatusSolicitacao.CANCELADA);
        entity.setRespondidaEm(LocalDateTime.now());
        return toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public SolicitacaoResponseDTO buscarPorId(
            Long id, String actorId, boolean administrador, boolean gestor) {
        Solicitacao entity = buscarEntidade(id);
        boolean responsavel = entity.getResponsavel().getId().toString().equals(actorId);
        boolean gestorResponsavel = entity.getGestor() != null
                && entity.getGestor().getId().toString().equals(actorId);
        boolean gestorConsultaPendente = gestor && entity.getStatus() == StatusSolicitacao.PENDENTE;
        if (!administrador && !responsavel && !gestorResponsavel && !gestorConsultaPendente) {
            throw new AccessDeniedException("Acesso não permitido.");
        }
        return toResponse(entity);
    }

    @Transactional(readOnly = true)
    public List<SolicitacaoResponseDTO> buscarTodos() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<SolicitacaoResponseDTO> buscarPorResponsavel(Long responsavelId) {
        return repository.findAllByResponsavelId(responsavelId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<SolicitacaoResponseDTO> buscarPorGestor(Long gestorId) {
        return repository.findAllByGestorId(gestorId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<SolicitacaoResponseDTO> buscarPendentesPorGestor(Long gestorId) {
        return buscarPendentesParaGestor();
    }

    @Transactional(readOnly = true)
    public List<SolicitacaoResponseDTO> buscarPendentesParaGestor() {
        return repository.findAllByStatus(StatusSolicitacao.PENDENTE)
                .stream().map(this::toResponse).toList();
    }

    private void validarPendente(Solicitacao entity) {
        if (entity.getStatus() != StatusSolicitacao.PENDENTE) {
            throw new BusinessException(
                    "Esta solicitação não pode ser alterada pois está com status: "
                            + entity.getStatus().name());
        }
    }

    private void validarResponsavel(Solicitacao entity, String actorId, boolean administrador) {
        if (!administrador && !entity.getResponsavel().getId().toString().equals(actorId)) {
            throw new AccessDeniedException("Acesso não permitido.");
        }
    }

    public Solicitacao buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitação", id));
    }

    private Solicitacao buscarEntidadeParaAtualizar(Long id) {
        return repository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitação", id));
    }

    private SolicitacaoResponseDTO toResponse(Solicitacao s) {
        return new SolicitacaoResponseDTO(
                s.getId(),
                s.getResponsavel().getId(),
                s.getResponsavel().getNome(),
                s.getGestor() != null ? s.getGestor().getId() : null,
                s.getGestor() != null ? s.getGestor().getNome() : null,
                s.getNoticia() != null ? s.getNoticia().getId() : null,
                s.getNoticia() != null ? s.getNoticia().getTitulo() : s.getTituloSubmetido(),
                s.getNoticia() != null ? s.getNoticia().getConteudo() : s.getConteudoSubmetido(),
                s.getNoticia() != null ? s.getNoticia().getImagemUrl() : s.getImagemSubmetida(),
                s.getNoticia() != null ? s.getNoticia().getPublicadaEm() : s.getPublicadaEmSubmetida(),
                s.getEmpresa() != null ? s.getEmpresa().getId()
                        : s.getNoticia() != null ? s.getNoticia().getEmpresa().getId() : null,
                s.getEmpresa() != null ? s.getEmpresa().getNome()
                        : s.getNoticia() != null ? s.getNoticia().getEmpresa().getNome() : null,
                s.getStatus(),
                s.getCriadaEm(),
                s.getRespondidaEm(),
                s.getMotivoRecusa()
        );
    }
}
