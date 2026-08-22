package br.nite.newsly.service;

import br.nite.newsly.dto.request.GestorRequestDTO;
import br.nite.newsly.dto.response.GestorResponseDTO;
import br.nite.newsly.entity.Gestor;
import br.nite.newsly.exception.ResourceNotFoundException;
import br.nite.newsly.mapper.GestorMapper;
import br.nite.newsly.repository.GestorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GestorService {

    private final GestorRepository repository;
    private final GestorMapper mapper;

    @Transactional
    public GestorResponseDTO criar(GestorRequestDTO dto) {
        Gestor entity = mapper.toEntity(dto);
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public GestorResponseDTO buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional(readOnly = true)
    public List<GestorResponseDTO> buscarTodos() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional
    public GestorResponseDTO atualizar(Long id, GestorRequestDTO dto) {
        Gestor entity = buscarEntidade(id);
        mapper.updateEntity(dto, entity);
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    public void excluir(Long id) {
        buscarEntidade(id);
        repository.deleteById(id);
    }


    public Gestor buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gestor", id));
    }
}
