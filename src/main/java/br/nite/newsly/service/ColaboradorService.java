package br.nite.newsly.service;

import br.nite.newsly.dto.request.ColaboradorRequestDTO;
import br.nite.newsly.dto.response.ColaboradorResponseDTO;
import br.nite.newsly.entity.Colaborador;
import br.nite.newsly.entity.EmpresaJr;
import br.nite.newsly.exception.ResourceNotFoundException;
import br.nite.newsly.mapper.ColaboradorMapper;
import br.nite.newsly.repository.ColaboradorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ColaboradorService {

    private final ColaboradorRepository repository;
    private final ColaboradorMapper mapper;
    private final EmpresaJrService empresaJrService;

    @Transactional
    public ColaboradorResponseDTO criar(ColaboradorRequestDTO dto) {
        Colaborador entity = mapper.toEntity(dto);

        return mapper.toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public ColaboradorResponseDTO buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional(readOnly = true)
    public List<ColaboradorResponseDTO> buscarTodos() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional
    public ColaboradorResponseDTO atualizar(Long id, ColaboradorRequestDTO dto) {
        Colaborador entity = buscarEntidade(id);
        mapper.updateEntity(dto, entity);

        if (dto.empresaId() != null) {
            EmpresaJr empresa = empresaJrService.buscarEntidade(dto.empresaId());
            entity.setEmpresa(empresa);
        } else {
            entity.setEmpresa(null);
        }

        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    public void excluir(Long id) {
        buscarEntidade(id);
        repository.deleteById(id);
    }

    public Colaborador buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Colaborador", id));
    }
}
