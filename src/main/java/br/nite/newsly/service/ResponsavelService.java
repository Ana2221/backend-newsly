package br.nite.newsly.service;

import br.nite.newsly.dto.request.ResponsavelRequestDTO;
import br.nite.newsly.dto.response.ResponsavelResponseDTO;
import br.nite.newsly.entity.EmpresaJr;
import br.nite.newsly.entity.Responsavel;
import br.nite.newsly.exception.BusinessException;
import br.nite.newsly.exception.ResourceNotFoundException;
import br.nite.newsly.mapper.ResponsavelMapper;
import br.nite.newsly.repository.ResponsavelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResponsavelService {

    private final ResponsavelRepository repository;
    private final ResponsavelMapper mapper;
    private final EmpresaJrService empresaJrService;

    @Transactional
    public ResponsavelResponseDTO criar(ResponsavelRequestDTO dto) {
        if (dto.empresaId() != null && repository.existsByEmpresaId(dto.empresaId())) {
            throw new BusinessException("A empresa já possui um responsável cadastrado.");
        }

        Responsavel entity = mapper.toEntity(dto);

        if (dto.empresaId() != null) {
            EmpresaJr empresa = empresaJrService.buscarEntidade(dto.empresaId());
            entity.setEmpresa(empresa);
        }

        return mapper.toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public ResponsavelResponseDTO buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional(readOnly = true)
    public List<ResponsavelResponseDTO> buscarTodos() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional
    public ResponsavelResponseDTO atualizar(Long id, ResponsavelRequestDTO dto) {
        Responsavel entity = buscarEntidade(id);
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

    public Responsavel buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Responsavel", id));
    }
}
