package br.nite.newsly.service;

import br.nite.newsly.dto.request.FuncaoRequestDTO;
import br.nite.newsly.dto.response.FuncaoResponseDTO;
import br.nite.newsly.entity.Funcao;
import br.nite.newsly.exception.BusinessException;
import br.nite.newsly.exception.ResourceNotFoundException;
import br.nite.newsly.mapper.FuncaoMapper;
import br.nite.newsly.repository.FuncaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FuncaoService {

    private final FuncaoRepository repository;
    private final FuncaoMapper mapper;

    @Transactional
    public FuncaoResponseDTO criar(FuncaoRequestDTO dto) {
        if (repository.existsByNome(dto.nome())) {
            throw new BusinessException("Já existe uma função com o nome: " + dto.nome());
        }
        Funcao entity = mapper.toEntity(dto);
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public FuncaoResponseDTO buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional(readOnly = true)
    public List<FuncaoResponseDTO> buscarTodos() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional
    public FuncaoResponseDTO atualizar(Long id, FuncaoRequestDTO dto) {
        Funcao entity = buscarEntidade(id);
        if (!entity.getNome().equals(dto.nome()) && repository.existsByNome(dto.nome())) {
            throw new BusinessException("Já existe uma função com o nome: " + dto.nome());
        }
        mapper.updateEntity(dto, entity);
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    public void excluir(Long id) {
        buscarEntidade(id);
        repository.deleteById(id);
    }

    public Funcao buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Função", id));
    }
}
