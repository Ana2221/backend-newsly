package br.nite.newsly.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record NoticiaRequestDTO(

        @NotBlank(message = "Título é obrigatório")
        @Size(max = 255, message = "Título deve ter no máximo 255 caracteres")
        String titulo,

        @NotBlank(message = "Conteúdo é obrigatório")
        String conteudo,

        @NotNull(message = "Data de publicação é obrigatória")
        LocalDateTime publicadaEm,

        @NotNull(message = "ID da empresa é obrigatório")
        Long empresaId
) {}
