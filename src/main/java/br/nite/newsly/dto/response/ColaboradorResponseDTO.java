package br.nite.newsly.dto.response;

import br.nite.newsly.enums.TipoUsuario;

import java.util.UUID;

public record ColaboradorResponseDTO(
        Long id,
        UUID matricula,
        String nome,
        Boolean atividade,
        ContatoResponseDTO contato,
        Long empresaId,
        TipoUsuario tipo
) implements UsuarioTipadoResponse {}
