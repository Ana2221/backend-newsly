package br.nite.newsly.service;

import br.nite.newsly.dto.request.EmpresaJrRequestDTO;
import br.nite.newsly.dto.request.ContatoRequestDTO;
import br.nite.newsly.dto.response.EmpresaJrResponseDTO;
import br.nite.newsly.dto.response.EmpresaJrPublicaResponseDTO;
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
import br.nite.newsly.security.SecurityAccess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class EmpresaJrService {

    private final EmpresaJrRepository repository;
    private final ResponsavelRepository respRepository;
    private final ColaboradorRepository colaboradorRepository;
    private final EmpresaJrMapper mapper;
    private final ArmazenamentoService armazenamentoService;
    private final SecurityAccess securityAccess;
    private final CnpjValidationService cnpjValidationService;

    @Transactional
    public EmpresaJrResponseDTO criar(EmpresaJrRequestDTO dto) {
        Long responsavelId = securityAccess.isCurrentResponsible()
                ? securityAccess.currentUserId()
                : dto.responsavelId();
        if (responsavelId == null) {
            throw new IllegalArgumentException("responsavelId é obrigatório para administradores.");
        }

        Responsavel resp = respRepository.findByIdForUpdate(responsavelId)
                .orElseThrow(() -> new ResourceNotFoundException("Responsável", responsavelId));
        if (resp.getEmpresa() != null) {
            throw new BusinessException("O responsável já possui uma empresa cadastrada.");
        }
        if (repository.existsByCnpj(dto.cnpj())) {
            throw new BusinessException("Já existe uma empresa com o CNPJ: " + dto.cnpj());
        }

        String email = requireCompanyEmail(dto.contato());
        if (repository.existsByContatoEmailIgnoreCase(email)) {
            throw new BusinessException("Já existe uma empresa com o e-mail: " + email);
        }
        cnpjValidationService.validar(dto.cnpj());

        EmpresaJr entity = mapper.toEntity(dto);
        entity.getContato().setEmail(email);
        entity.setResponsavel(resp);
        EmpresaJr savedEntity = repository.save(entity);
        resp.setEmpresa(entity);
        respRepository.save(resp);

        return mapper.toResponse(savedEntity);
    }

    @Transactional
    public EmpresaJrResponseDTO uploadLogo(Long id, MultipartFile arquivo) {
        requireOwnCompanyWhenResponsible(id);
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

    @Transactional(readOnly = true)
    public EmpresaJrPublicaResponseDTO buscarResumoPublicoPorId(Long id) {
        return resumoPublico(buscarEntidade(id));
    }

    @Transactional(readOnly = true)
    public List<EmpresaJrPublicaResponseDTO> buscarResumosPublicos() {
        return repository.findAll().stream()
                .map(this::resumoPublico)
                .toList();
    }

    @Transactional
    public void adicionarMembro(Long colaboradorId, Long empresaId) {
        requireOwnCompanyWhenResponsible(empresaId);
        Colaborador colaborador = colaboradorRepository.findById(colaboradorId)
                .orElseThrow(() -> new ResourceNotFoundException("Colaborador", colaboradorId));

        EmpresaJr empresa = repository.findById(empresaId)
                .orElseThrow(() -> new ResourceNotFoundException("EmpresaJr", empresaId));

        if (empresa.getColaboradores().contains(colaborador)) {
            throw new BusinessException("O colaborador já é membro desta empresa.");
        }
        if (securityAccess.isCurrentResponsible()
                && colaborador.getEmpresa() != null
                && !empresaId.equals(colaborador.getEmpresa().getId())) {
            throw new org.springframework.security.access.AccessDeniedException("Acesso não permitido.");
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
        requireOwnCompanyWhenResponsible(id);
        EmpresaJr entity = buscarEntidade(id);

        if (!entity.getCnpj().equals(dto.cnpj()) && repository.existsByCnpj(dto.cnpj())) {
            throw new BusinessException("Já existe uma empresa com o CNPJ: " + dto.cnpj());
        }
        if (!entity.getCnpj().equals(dto.cnpj())) {
            cnpjValidationService.validar(dto.cnpj());
        }
        if (dto.contato() != null && dto.contato().email() != null) {
            String email = requireCompanyEmail(dto.contato());
            if (repository.existsByContatoEmailIgnoreCaseAndIdNot(email, id)) {
                throw new BusinessException("Já existe uma empresa com o e-mail: " + email);
            }
        }

        mapper.updateEntity(dto, entity);
        if (dto.contato() != null && dto.contato().email() != null) {
            entity.getContato().setEmail(requireCompanyEmail(dto.contato()));
        }
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

    private void requireOwnCompanyWhenResponsible(Long empresaId) {
        if (securityAccess.isCurrentResponsible()) {
            securityAccess.requireOwnCompany(empresaId);
        }
    }

    private String requireCompanyEmail(ContatoRequestDTO contato) {
        if (contato == null || contato.email() == null || contato.email().isBlank()) {
            throw new IllegalArgumentException("O e-mail de contato da empresa é obrigatório.");
        }
        return contato.email().trim().toLowerCase(Locale.ROOT);
    }

    private EmpresaJrPublicaResponseDTO resumoPublico(EmpresaJr empresa) {
        return new EmpresaJrPublicaResponseDTO(
                empresa.getId(),
                empresa.getNome(),
                empresa.getCnpj(),
                empresa.getDescricao(),
                empresa.getLogoUrl());
    }
}
