package br.nite.newsly.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record DiretoriaRequestDTO(

        LocalDate criadaEm,

        @NotNull(message = "ID da empresa é obrigatório")
        Long empresaId

        
) {}
