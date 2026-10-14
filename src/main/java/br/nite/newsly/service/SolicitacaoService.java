package br.nite.newsly.service;

import br.nite.newsly.dto.request.ResponderSolicitacaoRequestDTO;
import br.nite.newsly.dto.request.SolicitacaoRequestDTO;
import br.nite.newsly.dto.response.SolicitacaoResponseDTO;
import br.nite.newsly.entity.Gestor;
import br.nite.newsly.entity.Noticia;
import br.nite.newsly.entity.Responsavel;
import br.nite.newsly.entity.Solicitacao;
import br.nite.newsly.enums.StatusSolicitacao;
import br.nite.newsly.exception.BusinessException;
import br.nite.newsly.exception.ResourceNotFoundException;
import br.nite.newsly.repository.SolicitacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SolicitacaoService {

    private final SolicitacaoRepository repository;
    private final ResponsavelService responsavelService;
    private final GestorService gestorService;
    private final NoticiaService noticiaService;

    @Transactional
    public SolicitacaoResponseDTO enviar(SolicitacaoRequestDTO dto) {
        if (repository.existsByNoticiaIdAndGestorIdAndStatus(
                dto.noticiaId(), dto.gestorId(), StatusSolicitacao.PENDENTE)) {
            throw new BusinessException(
                    "Já existe uma solicitação pendente para esta notícia com este gestor.");
        }

        Responsavel responsavel = responsavelService.buscarEntidade(dto.responsavelId());
        Gestor gestor = gestorService.buscarEntidade(dto.gestorId());
        Noticia noticia = noticiaService.buscarEntidade(dto.noticiaId());

        Solicitacao entity = new Solicitacao();
        entity.setResponsavel(responsavel);
        entity.setGestor(gestor);
        entity.setNoticia(noticia);
        entity.setStatus(StatusSolicitacao.PENDENTE);
        entity.setCriadaEm(LocalDateTime.now());

        return toResponse(repository.save(entity));
    }

    @Transactional
    public SolicitacaoResponseDTO aceitar(Long id) {
        Solicitacao entity = buscarEntidade(id);
        validarPendente(entity);
        entity.setStatus(StatusSolicitacao.ACEITA);
        entity.setRespondidaEm(LocalDateTime.now());
        return toResponse(repository.save(entity));
    }

    @Transactional
    public SolicitacaoResponseDTO recusar(Long id, ResponderSolicitacaoRequestDTO dto) {
        Solicitacao entity = buscarEntidade(id);
        validarPendente(entity);
        entity.setStatus(StatusSolicitacao.RECUSADA);
        entity.setRespondidaEm(LocalDateTime.now());
        entity.setMotivoRecusa(dto != null ? dto.motivoRecusa() : null);
        return toResponse(repository.save(entity));
    }

    @Transactional
    public SolicitacaoResponseDTO cancelar(Long id) {
        Solicitacao entity = buscarEntidade(id);
        validarPendente(entity);
        entity.setStatus(StatusSolicitacao.CANCELADA);
        entity.setRespondidaEm(LocalDateTime.now());
        return toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public SolicitacaoResponseDTO buscarPorId(Long id) {
        return toResponse(buscarEntidade(id));
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
        return repository.findAllByGestorIdAndStatus(gestorId, StatusSolicitacao.PENDENTE)
                .stream().map(this::toResponse).toList();
    }

    private void validarPendente(Solicitacao entity) {
        if (entity.getStatus() != StatusSolicitacao.PENDENTE) {
            throw new BusinessException(
                    "Esta solicitação não pode ser alterada pois está com status: "
                            + entity.getStatus().name());
        }
    }

    public Solicitacao buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitação", id));
    }

    private SolicitacaoResponseDTO toResponse(Solicitacao s) {
        return new SolicitacaoResponseDTO(
                s.getId(),
                s.getResponsavel().getId(),
                s.getResponsavel().getNome(),
                s.getGestor().getId(),
                s.getGestor().getNome(),
                s.getNoticia().getId(),
                s.getNoticia().getTitulo(),
                s.getStatus(),
                s.getCriadaEm(),
                s.getRespondidaEm(),
                s.getMotivoRecusa()
        );
    }
}
