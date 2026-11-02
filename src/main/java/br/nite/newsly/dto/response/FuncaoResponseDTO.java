package br.nite.newsly.dto.response;

public record FuncaoResponseDTO(
        Long id,
        String nome,
        String descricao,
        Long empresaId
) {}
