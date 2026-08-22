package br.nite.newsly.dto.response;

import br.nite.newsly.enums.TipoUsuario;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Resposta genérica para criação de qualquer tipo de usuário.
 * Apenas o campo referente ao tipo criado será preenchido;
 * os demais ficam nulos e são omitidos na serialização JSON.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CriarUsuarioResponseDTO(

        TipoUsuario tipo,

        UsuarioResponseDTO usuario,
        ColaboradorResponseDTO colaborador,
        GestorResponseDTO gestor,
        ResponsavelResponseDTO responsavel
) {

    public static CriarUsuarioResponseDTO deUsuario(UsuarioResponseDTO dto) {
        return new CriarUsuarioResponseDTO(TipoUsuario.USUARIO, dto, null, null, null);
    }

    public static CriarUsuarioResponseDTO deColaborador(ColaboradorResponseDTO dto) {
        return new CriarUsuarioResponseDTO(TipoUsuario.COLABORADOR, null, dto, null, null);
    }

    public static CriarUsuarioResponseDTO deGestor(GestorResponseDTO dto) {
        return new CriarUsuarioResponseDTO(TipoUsuario.GESTOR, null, null, dto, null);
    }

    public static CriarUsuarioResponseDTO deResponsavel(ResponsavelResponseDTO dto) {
        return new CriarUsuarioResponseDTO(TipoUsuario.RESPONSAVEL, null, null, null, dto);
    }
}
