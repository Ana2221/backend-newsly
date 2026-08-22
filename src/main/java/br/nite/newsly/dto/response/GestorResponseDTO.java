package br.nite.newsly.dto.response;

import br.nite.newsly.enums.Permissao;
import br.nite.newsly.enums.TipoUsuario;

import java.util.List;
import java.util.UUID;

public record GestorResponseDTO(
        Long id,
        UUID matricula,
        String nome,
        ContatoResponseDTO contato,
        List<Permissao> permissoes,
        TipoUsuario tipo
) implements UsuarioTipadoResponse {}
