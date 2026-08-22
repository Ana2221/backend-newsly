package br.nite.newsly.service;

import br.nite.newsly.dto.request.DiretorRequestDTO;
import br.nite.newsly.dto.response.DiretorResponseDTO;
import br.nite.newsly.entity.Colaborador;
import br.nite.newsly.entity.Diretor;
import br.nite.newsly.entity.Diretoria;
import br.nite.newsly.entity.Funcao;
import br.nite.newsly.exception.BusinessException;
import br.nite.newsly.exception.ResourceNotFoundException;
import br.nite.newsly.mapper.DiretorMapper;
import br.nite.newsly.repository.DiretorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DiretorService {

    private final DiretorRepository repository;
    private final DiretorMapper mapper;
    private final ColaboradorService colaboradorService;
    private final FuncaoService funcaoService;
    private final DiretoriaService diretoriaService;

    @Transactional
    public DiretorResponseDTO criar(DiretorRequestDTO dto) {
        if (repository.existsByColaboradorId(dto.colaboradorId())) {
            throw new BusinessException("O colaborador já é diretor.");
        }

        Colaborador colaborador = colaboradorService.buscarEntidade(dto.colaboradorId());
        Funcao funcao = funcaoService.buscarEntidade(dto.funcaoId());
        Diretoria diretoria = diretoriaService.buscarEntidade(dto.diretoriaId());

        Diretor entity = mapper.toEntity(dto);
        entity.setColaborador(colaborador);
        entity.setFuncao(funcao);
        entity.setDiretoria(diretoria);

        if (dto.permissoes() != null) {
            entity.setPermissoes(dto.permissoes());
        }

        return mapper.toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public DiretorResponseDTO buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional(readOnly = true)
    public List<DiretorResponseDTO> buscarTodos() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional
    public DiretorResponseDTO atualizar(Long id, DiretorRequestDTO dto) {
        Diretor entity = buscarEntidade(id);

        Funcao funcao = funcaoService.buscarEntidade(dto.funcaoId());
        Diretoria diretoria = diretoriaService.buscarEntidade(dto.diretoriaId());

        entity.setFuncao(funcao);
        entity.setDiretoria(diretoria);

        if (dto.permissoes() != null) {
            entity.setPermissoes(dto.permissoes());
        }

        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    public void excluir(Long id) {
        buscarEntidade(id);
        repository.deleteById(id);
    }

    public Diretor buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Diretor", id));
    }
}
