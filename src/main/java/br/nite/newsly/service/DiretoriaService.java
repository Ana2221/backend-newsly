package br.nite.newsly.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.nite.newsly.dto.request.DiretorDiretoriaRequestDTO;
import br.nite.newsly.dto.request.DiretoriaRequestDTO;
import br.nite.newsly.dto.response.DiretoriaResponseDTO;
import br.nite.newsly.entity.Colaborador;
import br.nite.newsly.entity.Diretor;
import br.nite.newsly.entity.Diretoria;
import br.nite.newsly.entity.EmpresaJr;
import br.nite.newsly.entity.Funcao;
import br.nite.newsly.exception.BusinessException;
import br.nite.newsly.exception.ResourceNotFoundException;
import br.nite.newsly.mapper.DiretoriaMapper;
import br.nite.newsly.repository.ColaboradorRepository;
import br.nite.newsly.repository.DiretorRepository;
import br.nite.newsly.repository.DiretoriaRepository;
import br.nite.newsly.repository.FuncaoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DiretoriaService {

    private final DiretoriaRepository repository;
    private final DiretoriaMapper mapper;
    private final EmpresaJrService empresaJrService;
    private final DiretorRepository diretorRepository;
    private final ColaboradorRepository colaboradorRepository;
    private final FuncaoRepository funcaoRepository;

    @Transactional
    public DiretoriaResponseDTO criar(DiretoriaRequestDTO dto) {
        EmpresaJr empresa = empresaJrService.buscarEntidade(dto.empresaId());

        Diretoria entity = mapper.toEntity(dto);
        entity.setEmpresa(empresa);
        entity.setDiretores(new ArrayList<>());

        Diretoria salva = repository.save(entity);

        // Cria os diretores junto com a diretoria se a lista for informada
        if (dto.diretores() != null && !dto.diretores().isEmpty()) {
            for (DiretorDiretoriaRequestDTO dirDto : dto.diretores()) {

                if (diretorRepository.existsByColaboradorId(dirDto.colaboradorId())) {
                    throw new BusinessException(
                            "O colaborador com id " + dirDto.colaboradorId() + " já é diretor.");
                }

                Colaborador colaborador = colaboradorRepository.findById(dirDto.colaboradorId())
                        .orElseThrow(() -> new ResourceNotFoundException("Colaborador", dirDto.colaboradorId()));

                Funcao funcao = funcaoRepository.findById(dirDto.funcaoId())
                        .orElseThrow(() -> new ResourceNotFoundException("Função", dirDto.funcaoId()));

                Diretor diretor = new Diretor();
                diretor.setColaborador(colaborador);
                diretor.setFuncao(funcao);
                diretor.setDiretoria(salva);
                diretor.setPermissoes(dirDto.permissoes() != null
                        ? dirDto.permissoes()
                        : new ArrayList<>());

                diretorRepository.save(diretor);
                salva.getDiretores().add(diretor);
            }
        }

        repository.save(salva);
        return mapper.toResponse(buscarEntidade(salva.getId()));
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
        repository.save(entity);
        return mapper.toResponse(buscarEntidade(id));
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
