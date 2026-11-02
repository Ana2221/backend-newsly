package br.nite.newsly.security;

import br.nite.newsly.repository.ColaboradorRepository;
import br.nite.newsly.repository.ComentarioRepository;
import br.nite.newsly.repository.DiretorRepository;
import br.nite.newsly.repository.DiretoriaRepository;
import br.nite.newsly.repository.EmpresaJrRepository;
import br.nite.newsly.repository.FuncaoRepository;
import br.nite.newsly.repository.NoticiaRepository;
import br.nite.newsly.repository.ResponsavelRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("securityAccess")
public class SecurityAccess {

    private final ResponsavelRepository responsavelRepository;
    private final EmpresaJrRepository empresaRepository;
    private final ColaboradorRepository colaboradorRepository;
    private final DiretoriaRepository diretoriaRepository;
    private final DiretorRepository diretorRepository;
    private final NoticiaRepository noticiaRepository;
    private final ComentarioRepository comentarioRepository;
    private final FuncaoRepository funcaoRepository;

    public SecurityAccess(
            ResponsavelRepository responsavelRepository,
            EmpresaJrRepository empresaRepository,
            ColaboradorRepository colaboradorRepository,
            DiretoriaRepository diretoriaRepository,
            DiretorRepository diretorRepository,
            NoticiaRepository noticiaRepository,
            ComentarioRepository comentarioRepository,
            FuncaoRepository funcaoRepository) {
        this.responsavelRepository = responsavelRepository;
        this.empresaRepository = empresaRepository;
        this.colaboradorRepository = colaboradorRepository;
        this.diretoriaRepository = diretoriaRepository;
        this.diretorRepository = diretorRepository;
        this.noticiaRepository = noticiaRepository;
        this.comentarioRepository = comentarioRepository;
        this.funcaoRepository = funcaoRepository;
    }

    public boolean isSelf(Authentication authentication, Long userId) {
        return authentication != null
                && userId != null
                && authentication.getName().equals(userId.toString());
    }

    public Long currentCompanyId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !hasRole(authentication, "ROLE_RESPONSAVEL")) {
            throw new AccessDeniedException("Acesso não permitido.");
        }
        Long responsavelId = Long.valueOf(authentication.getName());
        return responsavelRepository.findEmpresaIdById(responsavelId)
                .orElseThrow(() -> new AccessDeniedException("Responsável sem EJ associada."));
    }

    public boolean isCurrentResponsible() {
        return hasRole(SecurityContextHolder.getContext().getAuthentication(), "ROLE_RESPONSAVEL");
    }

    public boolean isCurrentManager() {
        return hasRole(SecurityContextHolder.getContext().getAuthentication(), "ROLE_GESTOR");
    }

    public boolean isCurrentAdmin() {
        return hasRole(SecurityContextHolder.getContext().getAuthentication(), "ROLE_ADMIN");
    }

    public Long currentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new AccessDeniedException("Acesso não permitido.");
        }
        return Long.valueOf(authentication.getName());
    }

    public void requireOwnCompany(Long empresaId) {
        if (empresaId == null || !empresaId.equals(currentCompanyId())) {
            throw new AccessDeniedException("Acesso não permitido.");
        }
    }

    public boolean isOwnCompany(Long empresaId) {
        return empresaId != null && empresaId.equals(currentCompanyId());
    }

    public boolean isOwnEmpresa(Long empresaId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return isOwnCompany(empresaId) && authentication != null
                && empresaRepository.existsByIdAndResponsavelId(
                        empresaId, Long.valueOf(authentication.getName()));
    }

    public boolean isOwnColaborador(Long colaboradorId) {
        return colaboradorRepository.existsByIdAndEmpresaId(colaboradorId, currentCompanyId());
    }

    public boolean isOwnDiretoria(Long diretoriaId) {
        return diretoriaRepository.existsByIdAndEmpresaId(diretoriaId, currentCompanyId());
    }

    public boolean isOwnDiretor(Long diretorId) {
        return diretorRepository.existsByIdAndDiretoriaEmpresaId(diretorId, currentCompanyId());
    }

    public boolean isOwnNoticia(Long noticiaId) {
        return noticiaRepository.existsByIdAndEmpresaId(noticiaId, currentCompanyId());
    }

    public boolean isOwnComentario(Long comentarioId) {
        return comentarioRepository.existsByIdAndNoticiaEmpresaId(comentarioId, currentCompanyId());
    }

    public boolean isOwnFuncao(Long funcaoId) {
        return funcaoRepository.existsByIdAndEmpresaId(funcaoId, currentCompanyId());
    }

    public boolean isOwnUsuario(Authentication authentication, Long userId) {
        return isSelf(authentication, userId)
                && responsavelRepository.existsById(userId);
    }

    public boolean hasRole(Authentication authentication, String role) {
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(role));
    }
}
