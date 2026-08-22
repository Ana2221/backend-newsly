package br.nite.newsly.dto.request;

import br.nite.newsly.enums.Permissao;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record DiretorRequestDTO(

        @NotNull(message = "ID do colaborador é obrigatório")
        Long colaboradorId,

        @NotNull(message = "ID da função é obrigatório")
        Long funcaoId,

        @NotNull(message = "ID da diretoria é obrigatório")
        Long diretoriaId,

        List<Permissao> permissoes
) {}
