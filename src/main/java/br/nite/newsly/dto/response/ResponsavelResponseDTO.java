package br.nite.newsly.dto.response;

import br.nite.newsly.enums.Permissao;
import br.nite.newsly.enums.TipoUsuario;

import java.util.List;
import java.util.UUID;

public record ResponsavelResponseDTO(
        Long id,
        UUID matricula,
        String nome,
        ContatoResponseDTO contato,
        List<Permissao> permissoes,
        Long empresaId,
        TipoUsuario tipo
) implements UsuarioTipadoResponse {}
