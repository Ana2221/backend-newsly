package br.nite.newsly.dto.response;

public record EmpresaJrPublicaResponseDTO(
        Long id,
        String nome,
        String cnpj,
        String descricao,
        String logoUrl
) {}
