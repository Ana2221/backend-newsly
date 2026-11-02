package br.nite.newsly.service;

import br.nite.newsly.dto.request.FuncaoRequestDTO;
import br.nite.newsly.dto.response.FuncaoResponseDTO;
import br.nite.newsly.entity.Funcao;
import br.nite.newsly.exception.BusinessException;
import br.nite.newsly.exception.ResourceNotFoundException;
import br.nite.newsly.mapper.FuncaoMapper;
import br.nite.newsly.repository.FuncaoRepository;
import br.nite.newsly.security.SecurityAccess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FuncaoService {

    private final FuncaoRepository repository;
    private final FuncaoMapper mapper;
    private final EmpresaJrService empresaJrService;
    private final SecurityAccess securityAccess;

    @Transactional
    public FuncaoResponseDTO criar(FuncaoRequestDTO dto) {
        Long empresaId = securityAccess.isCurrentResponsible()
                ? securityAccess.currentCompanyId()
                : dto.empresaId();
        if (securityAccess.isCurrentResponsible()) {
            securityAccess.requireOwnCompany(empresaId);
        }
        boolean nomeExistente = empresaId == null
                ? repository.existsByNome(dto.nome())
                : repository.existsByNomeAndEmpresaId(dto.nome(), empresaId);
        if (nomeExistente) {
            throw new BusinessException("Já existe uma função com o nome: " + dto.nome());
        }
        Funcao entity = mapper.toEntity(dto);
        if (empresaId != null) {
            entity.setEmpresa(empresaJrService.buscarEntidade(empresaId));
        }
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public FuncaoResponseDTO buscarPorId(Long id) {
        requireOwnFuncaoWhenResponsible(id);
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional(readOnly = true)
    public List<FuncaoResponseDTO> buscarTodos() {
        List<Funcao> funcoes = securityAccess.isCurrentResponsible()
                ? repository.findAllByEmpresaId(securityAccess.currentCompanyId())
                : repository.findAll();
        return funcoes.stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional
    public FuncaoResponseDTO atualizar(Long id, FuncaoRequestDTO dto) {
        requireOwnFuncaoWhenResponsible(id);
        Funcao entity = buscarEntidade(id);
        Long targetCompanyId = securityAccess.isCurrentResponsible()
                ? securityAccess.currentCompanyId()
                : dto.empresaId() != null ? dto.empresaId()
                : entity.getEmpresa() == null ? null : entity.getEmpresa().getId();
        boolean nomeExistente = targetCompanyId == null
                ? repository.existsByNome(dto.nome())
                : repository.existsByNomeAndEmpresaId(dto.nome(), targetCompanyId);
        if (!entity.getNome().equals(dto.nome()) && nomeExistente) {
            throw new BusinessException("Já existe uma função com o nome: " + dto.nome());
        }
        mapper.updateEntity(dto, entity);
        if (!securityAccess.isCurrentResponsible() && dto.empresaId() != null) {
            entity.setEmpresa(empresaJrService.buscarEntidade(dto.empresaId()));
        }
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

    private void requireOwnFuncaoWhenResponsible(Long id) {
        if (securityAccess.isCurrentResponsible() && !securityAccess.isOwnFuncao(id)) {
            throw new org.springframework.security.access.AccessDeniedException("Acesso não permitido.");
        }
    }
}
