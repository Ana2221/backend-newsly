package br.nite.newsly.service;

import br.nite.newsly.dto.request.NoticiaRequestDTO;
import br.nite.newsly.dto.response.NoticiaResponseDTO;
import br.nite.newsly.entity.EmpresaJr;
import br.nite.newsly.entity.Noticia;
import br.nite.newsly.enums.StatusSolicitacao;
import br.nite.newsly.exception.ResourceNotFoundException;
import br.nite.newsly.mapper.NoticiaMapper;
import br.nite.newsly.repository.NoticiaRepository;
import br.nite.newsly.security.SecurityAccess;
import br.nite.newsly.service.storage.ArmazenamentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticiaService {

    private final NoticiaRepository repository;
    private final NoticiaMapper mapper;
    private final EmpresaJrService empresaJrService;
    private final ArmazenamentoService armazenamentoService;
    private final SecurityAccess securityAccess;

    @Transactional
    public NoticiaResponseDTO criar(NoticiaRequestDTO dto) {
        requireOwnCompanyWhenResponsible(dto.empresaId());
        EmpresaJr empresa = empresaJrService.buscarEntidade(dto.empresaId());
        Noticia entity = mapper.toEntity(dto);
        entity.setEmpresa(empresa);
        if (securityAccess.isCurrentResponsible()) {
            entity.setAprovada(false);
        }
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    public Noticia criarAprovada(NoticiaRequestDTO dto) {
        EmpresaJr empresa = empresaJrService.buscarEntidade(dto.empresaId());
        Noticia entity = mapper.toEntity(dto);
        entity.setEmpresa(empresa);
        entity.setAprovada(true);
        return repository.save(entity);
    }

    @Transactional
    public NoticiaResponseDTO uploadImagem(Long id, MultipartFile arquivo) {
        requireOwnNoticiaWhenResponsible(id);
        Noticia entity = buscarEntidade(id);

        if (entity.getImagemUrl() != null) {
            armazenamentoService.remover(entity.getImagemUrl());
        }

        String caminho = armazenamentoService.salvar(arquivo, "noticias");
        entity.setImagemUrl(caminho);
        if (securityAccess.isCurrentResponsible()) {
            entity.setAprovada(false);
        }

        return mapper.toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public NoticiaResponseDTO buscarPorId(Long id) {
        requireOwnNoticiaWhenResponsible(id);
        Noticia noticia;
        if (securityAccess.isCurrentResponsible() || securityAccess.isCurrentAdmin()) {
            noticia = buscarEntidade(id);
        } else if (securityAccess.isCurrentManager()) {
            noticia = repository.findAprovadaOuPendenteParaGestor(id, StatusSolicitacao.PENDENTE)
                    .orElseThrow(() -> new ResourceNotFoundException("Noticia", id));
        } else {
            noticia = repository.findPublicadaById(id, StatusSolicitacao.ACEITA)
                    .orElseThrow(() -> new ResourceNotFoundException("Noticia", id));
        }
        return mapper.toResponse(noticia);
    }

    @Transactional(readOnly = true)
    public List<NoticiaResponseDTO> buscarTodos() {
        List<Noticia> noticias;
        if (securityAccess.isCurrentResponsible()) {
            noticias = repository.findAllByEmpresaId(securityAccess.currentCompanyId());
        } else if (securityAccess.isCurrentAdmin()) {
            noticias = repository.findAll();
        } else if (securityAccess.isCurrentManager()) {
            noticias = repository.findAllAprovadasOuPendentes(StatusSolicitacao.PENDENTE);
        } else {
            noticias = repository.findAllPublicadas(StatusSolicitacao.ACEITA);
        }
        return noticias.stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional
    public NoticiaResponseDTO atualizar(Long id, NoticiaRequestDTO dto) {
        requireOwnNoticiaWhenResponsible(id);
        requireOwnCompanyWhenResponsible(dto.empresaId());
        Noticia entity = buscarEntidade(id);
        EmpresaJr empresa = empresaJrService.buscarEntidade(dto.empresaId());
        mapper.updateEntity(dto, entity);
        entity.setEmpresa(empresa);
        if (securityAccess.isCurrentResponsible()) {
            entity.setAprovada(false);
        }
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    public void aprovarPublicacao(Long id) {
        Noticia noticia = buscarEntidade(id);
        noticia.setAprovada(true);
        repository.save(noticia);
    }

    @Transactional
    public void excluir(Long id) {
        Noticia entity = buscarEntidade(id);
        if (entity.getImagemUrl() != null) {
            armazenamentoService.remover(entity.getImagemUrl());
        }
        repository.deleteById(id);
    }

    public Noticia buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Noticia", id));
    }

    public EmpresaJr buscarEmpresaParaSubmissao(Long empresaId) {
        requireOwnCompanyWhenResponsible(empresaId);
        return empresaJrService.buscarEntidade(empresaId);
    }

    private void requireOwnCompanyWhenResponsible(Long empresaId) {
        if (securityAccess.isCurrentResponsible()) {
            securityAccess.requireOwnCompany(empresaId);
        }
    }

    private void requireOwnNoticiaWhenResponsible(Long id) {
        if (securityAccess.isCurrentResponsible() && !securityAccess.isOwnNoticia(id)) {
            throw new org.springframework.security.access.AccessDeniedException("Acesso não permitido.");
        }
    }
}
