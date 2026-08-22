package br.nite.newsly.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ColaboradorRequestDTO(

        String nome,

        String senha,

        ContatoRequestDTO contato,

        Boolean atividade,

        Long empresaId
) {}
