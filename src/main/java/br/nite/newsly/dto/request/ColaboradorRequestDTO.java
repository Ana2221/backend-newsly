package br.nite.newsly.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ColaboradorRequestDTO(

        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 150, message = "Nome deve ter no máximo 150 caracteres")
        String nome,

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 6, max = 255, message = "Senha deve ter entre 6 e 255 caracteres")
        String senha,

        @Valid
        ContatoRequestDTO contato,

        Boolean atividade,

        Long empresaId
) {}
