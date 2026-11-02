package br.nite.newsly.service;

import br.nite.newsly.dto.request.ComentarioRequestDTO;
import br.nite.newsly.dto.response.ComentarioResponseDTO;
import br.nite.newsly.entity.Comentario;
import br.nite.newsly.entity.Noticia;
import br.nite.newsly.exception.ResourceNotFoundException;
import br.nite.newsly.mapper.ComentarioMapper;
import br.nite.newsly.repository.ComentarioRepository;
import br.nite.newsly.security.SecurityAccess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ComentarioService {

    private final ComentarioRepository repository;
    private final ComentarioMapper mapper;
    private final NoticiaService noticiaService;
    private final SecurityAccess securityAccess;

    @Transactional
    public ComentarioResponseDTO criar(ComentarioRequestDTO dto) {
        requireOwnNoticiaWhenResponsible(dto.noticiaId());
        Noticia noticia = noticiaService.buscarEntidade(dto.noticiaId());

        Comentario entity = mapper.toEntity(dto);
        entity.setNoticia(noticia);

        // Relacionamento recursivo: associa ao comentário pai se informado
        if (dto.comentarioPaiId() != null) {
            Comentario pai = buscarEntidade(dto.comentarioPaiId());
            requireOwnComentarioWhenResponsible(pai.getId());
            if (!pai.getNoticia().getId().equals(dto.noticiaId())) {
                throw new org.springframework.security.access.AccessDeniedException("Acesso não permitido.");
            }
            entity.setComentarioPai(pai);
        }

        return mapper.toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public ComentarioResponseDTO buscarPorId(Long id) {
        requireOwnComentarioWhenResponsible(id);
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional(readOnly = true)
    public List<ComentarioResponseDTO> buscarTodos() {
        List<Comentario> comentarios = securityAccess.isCurrentResponsible()
                ? repository.findAllByNoticiaEmpresaId(securityAccess.currentCompanyId())
                : repository.findAll();
        return comentarios.stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ComentarioResponseDTO> buscarPorNoticia(Long noticiaId) {
        requireOwnNoticiaWhenResponsible(noticiaId);
        noticiaService.buscarEntidade(noticiaId);
        return repository.findAllByNoticiaIdAndComentarioPaiIsNull(noticiaId).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional
    public ComentarioResponseDTO atualizar(Long id, ComentarioRequestDTO dto) {
        requireOwnComentarioWhenResponsible(id);
        requireOwnNoticiaWhenResponsible(dto.noticiaId());
        Comentario entity = buscarEntidade(id);
        Noticia noticia = noticiaService.buscarEntidade(dto.noticiaId());

        mapper.updateEntity(dto, entity);
        entity.setNoticia(noticia);

        if (dto.comentarioPaiId() != null) {
            Comentario pai = buscarEntidade(dto.comentarioPaiId());
            requireOwnComentarioWhenResponsible(pai.getId());
            if (!pai.getNoticia().getId().equals(dto.noticiaId())) {
                throw new org.springframework.security.access.AccessDeniedException("Acesso não permitido.");
            }
            entity.setComentarioPai(pai);
        } else {
            entity.setComentarioPai(null);
        }

        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    public void excluir(Long id) {
        buscarEntidade(id);
        repository.deleteById(id);
    }

    public Comentario buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comentario", id));
    }

    private void requireOwnNoticiaWhenResponsible(Long noticiaId) {
        if (securityAccess.isCurrentResponsible() && !securityAccess.isOwnNoticia(noticiaId)) {
            throw new org.springframework.security.access.AccessDeniedException("Acesso não permitido.");
        }
    }

    private void requireOwnComentarioWhenResponsible(Long comentarioId) {
        if (securityAccess.isCurrentResponsible() && !securityAccess.isOwnComentario(comentarioId)) {
            throw new org.springframework.security.access.AccessDeniedException("Acesso não permitido.");
        }
    }
}
