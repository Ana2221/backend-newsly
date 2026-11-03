package br.nite.newsly.service;

import br.nite.newsly.dto.request.ColaboradorRequestDTO;
import br.nite.newsly.dto.request.CriarUsuarioRequestDTO;
import br.nite.newsly.dto.request.GestorRequestDTO;
import br.nite.newsly.dto.request.ResponsavelRequestDTO;
import br.nite.newsly.dto.request.UsuarioRequestDTO;
import br.nite.newsly.dto.response.CriarUsuarioResponseDTO;
import br.nite.newsly.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Adapter que recebe um DTO unificado com o tipo de usuário
 * e delega a criação para o service especializado correto.
 */
@Service
@RequiredArgsConstructor
public class UsuarioAdapterService {

    private final UsuarioService usuarioService;
    private final ColaboradorService colaboradorService;
    private final GestorService gestorService;
    private final ResponsavelService responsavelService;

    @Transactional
    public CriarUsuarioResponseDTO criar(CriarUsuarioRequestDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean administrador = authentication != null && authentication.isAuthenticated()
                && authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        boolean cadastroPublicoPermitido = dto.tipo() == br.nite.newsly.enums.TipoUsuario.USUARIO
                || dto.tipo() == br.nite.newsly.enums.TipoUsuario.COLABORADOR
                || dto.tipo() == br.nite.newsly.enums.TipoUsuario.RESPONSAVEL;
        if (!cadastroPublicoPermitido && !administrador) {
            throw new AccessDeniedException("Somente um administrador pode criar perfis privilegiados.");
        }
        if (dto.tipo() == br.nite.newsly.enums.TipoUsuario.COLABORADOR
                && !administrador && dto.empresaId() != null) {
            throw new AccessDeniedException("O cadastro público de colaborador não pode associar uma EJ.");
        }
        if (dto.tipo() == br.nite.newsly.enums.TipoUsuario.RESPONSAVEL && !administrador
                && (dto.empresaId() != null || (dto.permissoes() != null && !dto.permissoes().isEmpty()))) {
            throw new AccessDeniedException(
                    "O cadastro público de responsável não pode associar uma EJ ou definir permissões.");
        }

        return switch (dto.tipo()) {
            case USUARIO -> {
                var request = new UsuarioRequestDTO(
                        dto.nome(),
                        dto.senha(),
                        dto.contato()
                );
                yield CriarUsuarioResponseDTO.deUsuario(usuarioService.criar(request));
            }
            case COLABORADOR -> {
                var request = new ColaboradorRequestDTO(
                        dto.nome(),
                        dto.senha(),
                        dto.contato(),
                        true,
                        administrador ? dto.empresaId() : null
                );
                yield CriarUsuarioResponseDTO.deColaborador(colaboradorService.criar(request));
            }
            case GESTOR -> {
                var request = new GestorRequestDTO(
                        dto.nome(),
                        dto.senha(),
                        dto.contato(),
                        dto.permissoes()
                );
                yield CriarUsuarioResponseDTO.deGestor(gestorService.criar(request));
            }
            case RESPONSAVEL -> {
                var request = new ResponsavelRequestDTO(
                        dto.nome(),
                        dto.senha(),
                        dto.contato(),
                        administrador ? dto.permissoes() : null,
                        administrador ? dto.empresaId() : null
                );
                yield CriarUsuarioResponseDTO.deResponsavel(responsavelService.criar(request));
            }
        };
    }
}
