package br.nite.newsly.service;

import br.nite.newsly.dto.request.NoticiaRequestDTO;
import br.nite.newsly.dto.response.NoticiaResponseDTO;
import br.nite.newsly.entity.EmpresaJr;
import br.nite.newsly.entity.Noticia;
import br.nite.newsly.exception.ResourceNotFoundException;
import br.nite.newsly.mapper.NoticiaMapper;
import br.nite.newsly.repository.NoticiaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticiaService {

    private final NoticiaRepository repository;
    private final NoticiaMapper mapper;
    private final EmpresaJrService empresaJrService;

    @Transactional
    public NoticiaResponseDTO criar(NoticiaRequestDTO dto) {
        EmpresaJr empresa = empresaJrService.buscarEntidade(dto.empresaId());
        Noticia entity = mapper.toEntity(dto);
        entity.setEmpresa(empresa);
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public NoticiaResponseDTO buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional(readOnly = true)
    public List<NoticiaResponseDTO> buscarTodos() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional
    public NoticiaResponseDTO atualizar(Long id, NoticiaRequestDTO dto) {
        Noticia entity = buscarEntidade(id);
        EmpresaJr empresa = empresaJrService.buscarEntidade(dto.empresaId());
        mapper.updateEntity(dto, entity);
        entity.setEmpresa(empresa);
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    public void excluir(Long id) {
        buscarEntidade(id);
        repository.deleteById(id);
    }

    public Noticia buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Noticia", id));
    }
}
