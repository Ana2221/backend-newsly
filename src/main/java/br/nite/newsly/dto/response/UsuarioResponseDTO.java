package br.nite.newsly.dto.response;

public record UsuarioResponseDTO(
        Long id,
        String nome,
        ContatoResponseDTO contato
) {}
