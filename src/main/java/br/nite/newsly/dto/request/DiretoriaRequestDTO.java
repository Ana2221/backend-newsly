package br.nite.newsly.dto.request;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record DiretoriaRequestDTO(

        String nome,
        @NotNull(message = "Data de criação é obrigatória")
        LocalDate criadaEm,

        @NotNull(message = "ID da empresa é obrigatório")
        Long empresaId,

        // Lista opcional de diretores a serem criados junto com a diretoria
        @Valid
        List<DiretorDiretoriaRequestDTO> diretores
) {}
