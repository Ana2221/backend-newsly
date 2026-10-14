package br.nite.newsly.dto.request;

import jakarta.validation.constraints.Size;

public record ResponderSolicitacaoRequestDTO(

        @Size(max = 500, message = "Motivo deve ter no máximo 500 caracteres")
        String motivoRecusa
) {}
