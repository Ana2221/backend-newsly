package br.nite.newsly.service;

import br.nite.newsly.dto.request.DiretoriaRequestDTO;
import br.nite.newsly.dto.response.DiretoriaResponseDTO;
import br.nite.newsly.entity.Diretoria;
import br.nite.newsly.entity.EmpresaJr;
import br.nite.newsly.exception.ResourceNotFoundException;
import br.nite.newsly.mapper.DiretoriaMapper;
import br.nite.newsly.repository.DiretoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DiretoriaService {

    private final DiretoriaRepository repository;
    private final DiretoriaMapper mapper;
    private final EmpresaJrService empresaJrService;

    @Transactional
    public DiretoriaResponseDTO criar(DiretoriaRequestDTO dto) {
        EmpresaJr empresa = empresaJrService.buscarEntidade(dto.empresaId());
        Diretoria entity = mapper.toEntity(dto);
        entity.setEmpresa(empresa);
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public DiretoriaResponseDTO buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional(readOnly = true)
    public List<DiretoriaResponseDTO> buscarTodos() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional
    public DiretoriaResponseDTO atualizar(Long id, DiretoriaRequestDTO dto) {
        Diretoria entity = buscarEntidade(id);
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

    public Diretoria buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Diretoria", id));
    }
}
