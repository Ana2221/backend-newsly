package br.nite.newsly.service;

import br.nite.newsly.dto.request.ComentarioRequestDTO;
import br.nite.newsly.dto.response.ComentarioResponseDTO;
import br.nite.newsly.entity.Comentario;
import br.nite.newsly.entity.Noticia;
import br.nite.newsly.exception.ResourceNotFoundException;
import br.nite.newsly.mapper.ComentarioMapper;
import br.nite.newsly.repository.ComentarioRepository;
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

    @Transactional
    public ComentarioResponseDTO criar(ComentarioRequestDTO dto) {
        Noticia noticia = noticiaService.buscarEntidade(dto.noticiaId());

        Comentario entity = mapper.toEntity(dto);
        entity.setNoticia(noticia);

        // Relacionamento recursivo: associa ao comentário pai se informado
        if (dto.comentarioPaiId() != null) {
            Comentario pai = buscarEntidade(dto.comentarioPaiId());
            entity.setComentarioPai(pai);
        }

        return mapper.toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public ComentarioResponseDTO buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional(readOnly = true)
    public List<ComentarioResponseDTO> buscarTodos() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ComentarioResponseDTO> buscarPorNoticia(Long noticiaId) {
        noticiaService.buscarEntidade(noticiaId);
        return repository.findAllByNoticiaIdAndComentarioPaiIsNull(noticiaId).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional
    public ComentarioResponseDTO atualizar(Long id, ComentarioRequestDTO dto) {
        Comentario entity = buscarEntidade(id);
        Noticia noticia = noticiaService.buscarEntidade(dto.noticiaId());

        mapper.updateEntity(dto, entity);
        entity.setNoticia(noticia);

        if (dto.comentarioPaiId() != null) {
            Comentario pai = buscarEntidade(dto.comentarioPaiId());
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
}
