package br.nite.newsly.dto.request;

import jakarta.validation.constraints.NotNull;

public record SolicitacaoRequestDTO(

        @NotNull(message = "ID do responsável é obrigatório")
        Long responsavelId,

        @NotNull(message = "ID do gestor é obrigatório")
        Long gestorId,

        @NotNull(message = "ID da notícia é obrigatório")
        Long noticiaId
) {}
