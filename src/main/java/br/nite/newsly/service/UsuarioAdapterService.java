package br.nite.newsly.service;

import br.nite.newsly.dto.request.ColaboradorRequestDTO;
import br.nite.newsly.dto.request.CriarUsuarioRequestDTO;
import br.nite.newsly.dto.request.GestorRequestDTO;
import br.nite.newsly.dto.request.ResponsavelRequestDTO;
import br.nite.newsly.dto.request.UsuarioRequestDTO;
import br.nite.newsly.dto.response.CriarUsuarioResponseDTO;
import br.nite.newsly.exception.BusinessException;
import lombok.RequiredArgsConstructor;
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
                        dto.empresaId()
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
                        dto.permissoes(),
                        dto.empresaId()
                );
                yield CriarUsuarioResponseDTO.deResponsavel(responsavelService.criar(request));
            }
        };
    }
}
