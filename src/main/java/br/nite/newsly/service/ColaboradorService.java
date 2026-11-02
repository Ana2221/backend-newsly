package br.nite.newsly.service;

import br.nite.newsly.dto.request.ColaboradorRequestDTO;
import br.nite.newsly.dto.response.ColaboradorResponseDTO;
import br.nite.newsly.entity.Colaborador;
import br.nite.newsly.entity.EmpresaJr;
import br.nite.newsly.exception.ResourceNotFoundException;
import br.nite.newsly.mapper.ColaboradorMapper;
import br.nite.newsly.repository.ColaboradorRepository;
import br.nite.newsly.security.SecurityAccess;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ColaboradorService {

    private final ColaboradorRepository repository;
    private final ColaboradorMapper mapper;
    private final EmpresaJrService empresaJrService;
    private final PasswordEncoder passwordEncoder;
    private final SecurityAccess securityAccess;

    @Transactional
    public ColaboradorResponseDTO criar(ColaboradorRequestDTO dto) {
        Colaborador entity = mapper.toEntity(dto);
        entity.setSenha(passwordEncoder.encode(dto.senha()));
        if (securityAccess.isCurrentResponsible()) {
            entity.setEmpresa(empresaJrService.buscarEntidade(securityAccess.currentCompanyId()));
        } else if (dto.empresaId() != null) {
            entity.setEmpresa(empresaJrService.buscarEntidade(dto.empresaId()));
        }

        return mapper.toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public ColaboradorResponseDTO buscarPorId(Long id) {
        requireOwnColaboradorWhenResponsible(id);
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional(readOnly = true)
    public List<ColaboradorResponseDTO> buscarTodos() {
        List<Colaborador> colaboradores = securityAccess.isCurrentResponsible()
                ? repository.findAllByEmpresaId(securityAccess.currentCompanyId())
                : repository.findAll();
        return colaboradores.stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional
    public ColaboradorResponseDTO atualizar(Long id, ColaboradorRequestDTO dto) {
        requireOwnColaboradorWhenResponsible(id);
        Colaborador entity = buscarEntidade(id);
        mapper.updateEntity(dto, entity);
        if (dto.senha() != null) {
            entity.setSenha(passwordEncoder.encode(dto.senha()));
        }

        if (securityAccess.isCurrentResponsible()) {
            entity.setEmpresa(empresaJrService.buscarEntidade(securityAccess.currentCompanyId()));
        } else if (dto.empresaId() != null) {
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

    private void requireOwnColaboradorWhenResponsible(Long id) {
        if (securityAccess.isCurrentResponsible() && !securityAccess.isOwnColaborador(id)) {
            throw new org.springframework.security.access.AccessDeniedException("Acesso não permitido.");
        }
    }
}
