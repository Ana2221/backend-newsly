package br.nite.newsly.service;

import br.nite.newsly.dto.request.EmpresaJrRequestDTO;
import br.nite.newsly.dto.response.EmpresaJrResponseDTO;
import br.nite.newsly.entity.Colaborador;
import br.nite.newsly.entity.EmpresaJr;
import br.nite.newsly.entity.Responsavel;
import br.nite.newsly.exception.BusinessException;
import br.nite.newsly.exception.ResourceNotFoundException;
import br.nite.newsly.mapper.EmpresaJrMapper;
import br.nite.newsly.repository.ColaboradorRepository;
import br.nite.newsly.repository.EmpresaJrRepository;
import br.nite.newsly.repository.ResponsavelRepository;
import br.nite.newsly.service.storage.ArmazenamentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmpresaJrService {

    private final EmpresaJrRepository repository;
    private final ResponsavelRepository respRepository;
    private final ColaboradorRepository colaboradorRepository;
    private final EmpresaJrMapper mapper;
    private final ArmazenamentoService armazenamentoService;

    @Transactional
    public EmpresaJrResponseDTO criar(EmpresaJrRequestDTO dto) {
        if (repository.existsByCnpj(dto.cnpj())) {
            throw new BusinessException("Já existe uma empresa com o CNPJ: " + dto.cnpj());
        }

        Responsavel resp = respRepository.findById(dto.responsavelId())
                .orElseThrow(() -> new ResourceNotFoundException("Responsável", dto.responsavelId()));

        EmpresaJr entity = mapper.toEntity(dto);
        entity.setResponsavel(resp);
        resp.setEmpresa(entity);
        respRepository.save(resp);

        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    public EmpresaJrResponseDTO uploadLogo(Long id, MultipartFile arquivo) {
        EmpresaJr entity = buscarEntidade(id);

        if (entity.getLogoUrl() != null) {
            armazenamentoService.remover(entity.getLogoUrl());
        }

        String caminho = armazenamentoService.salvar(arquivo, "logos");
        entity.setLogoUrl(caminho);

        return mapper.toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public EmpresaJrResponseDTO buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public void adicionarMembro(Long colaboradorId, Long empresaId) {
        Colaborador colaborador = colaboradorRepository.findById(colaboradorId)
                .orElseThrow(() -> new ResourceNotFoundException("Colaborador", colaboradorId));

        EmpresaJr empresa = repository.findById(empresaId)
                .orElseThrow(() -> new ResourceNotFoundException("EmpresaJr", empresaId));

        if (empresa.getColaboradores().contains(colaborador)) {
            throw new BusinessException("O colaborador já é membro desta empresa.");
        }

        empresa.getColaboradores().add(colaborador);
        colaborador.setEmpresa(empresa);
        colaboradorRepository.save(colaborador);
        repository.save(empresa);
    }

    @Transactional(readOnly = true)
    public List<EmpresaJrResponseDTO> buscarTodos() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Transactional
    public EmpresaJrResponseDTO atualizar(Long id, EmpresaJrRequestDTO dto) {
        EmpresaJr entity = buscarEntidade(id);

        if (!entity.getCnpj().equals(dto.cnpj()) && repository.existsByCnpj(dto.cnpj())) {
            throw new BusinessException("Já existe uma empresa com o CNPJ: " + dto.cnpj());
        }

        mapper.updateEntity(dto, entity);
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    public void excluir(Long id) {
        EmpresaJr entity = buscarEntidade(id);
        if (entity.getLogoUrl() != null) {
            armazenamentoService.remover(entity.getLogoUrl());
        }
        repository.deleteById(id);
    }

    public EmpresaJr buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EmpresaJr", id));
    }
}
