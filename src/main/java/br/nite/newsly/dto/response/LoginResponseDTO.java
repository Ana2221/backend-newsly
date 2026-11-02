package br.nite.newsly.dto.response;

import java.util.List;

public record LoginResponseDTO(
        String accessToken,
        String tokenType,
        long expiresIn,
        AuthenticatedUserDTO usuario
) {
    public record AuthenticatedUserDTO(
            Long id,
            String nome,
            String email,
            String tipo,
            List<String> permissoes
    ) {}
}
