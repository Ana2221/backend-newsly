package br.nite.newsly.service;

import br.nite.newsly.dto.response.CriarUsuarioResponseDTO;
import br.nite.newsly.dto.response.UsuarioTipadoResponse;
import br.nite.newsly.entity.Colaborador;
import br.nite.newsly.entity.Gestor;
import br.nite.newsly.entity.Responsavel;
import br.nite.newsly.entity.Usuario;
import br.nite.newsly.exception.BusinessException;
import br.nite.newsly.mapper.ColaboradorMapper;
import br.nite.newsly.mapper.GestorMapper;
import br.nite.newsly.mapper.ResponsavelMapper;
import br.nite.newsly.mapper.UsuarioMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Adapter de saída: recebe uma entidade Usuario (ou subclasse),
 * identifica o tipo real via instanceof e constrói o response
 * especializado correto para cada subtipo.
 */
@Service
@RequiredArgsConstructor
public class UsuarioOutputAdapterService {

    private final UsuarioMapper usuarioMapper;
    private final ColaboradorMapper colaboradorMapper;
    private final GestorMapper gestorMapper;
    private final ResponsavelMapper responsavelMapper;

    /**
     * Usado nos GETs — retorna o wrapper CriarUsuarioResponseDTO
     * com campo "tipo" e os dados da subclasse correspondente.
     */
    public CriarUsuarioResponseDTO adaptar(Usuario usuario) {
        if (usuario instanceof Colaborador colaborador) {
            return CriarUsuarioResponseDTO.deColaborador(colaboradorMapper.toResponse(colaborador));
        }
        if (usuario instanceof Gestor gestor) {
            return CriarUsuarioResponseDTO.deGestor(gestorMapper.toResponse(gestor));
        }
        if (usuario instanceof Responsavel responsavel) {
            return CriarUsuarioResponseDTO.deResponsavel(responsavelMapper.toResponse(responsavel));
        }
        return CriarUsuarioResponseDTO.deUsuario(usuarioMapper.toResponse(usuario));
    }

    /**
     * Usado no login — retorna diretamente o DTO concreto da subclasse
     * (ColaboradorResponseDTO, GestorResponseDTO ou ResponsavelResponseDTO),
     * com contrato garantido pela interface UsuarioTipadoResponse.
     */
    public UsuarioTipadoResponse adaptarParaLogin(Usuario usuario) {
        if (usuario instanceof Colaborador colaborador) {
            return colaboradorMapper.toResponse(colaborador);
        }
        if (usuario instanceof Gestor gestor) {
            return gestorMapper.toResponse(gestor);
        }
        if (usuario instanceof Responsavel responsavel) {
            return responsavelMapper.toResponse(responsavel);
        }
        throw new BusinessException("Tipo de usuário não autorizado para login.");
    }
}
