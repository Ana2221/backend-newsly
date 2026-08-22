package br.nite.newsly.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ComentarioRequestDTO(

        @NotBlank(message = "Conteúdo é obrigatório")
        String conteudo,

        @NotNull(message = "Data de publicação é obrigatória")
        LocalDateTime publicadoEm,

        @NotNull(message = "ID da notícia é obrigatório")
        Long noticiaId,

        // Opcional: ID do comentário pai (para respostas)
        Long comentarioPaiId
) {}
