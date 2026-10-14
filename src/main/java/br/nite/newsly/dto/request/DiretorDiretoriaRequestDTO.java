package br.nite.newsly.dto.request;

import br.nite.newsly.enums.Permissao;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * DTO interno usado ao criar uma Diretoria com seus Diretores já definidos.
 * Não inclui diretoriaId pois a diretoria está sendo criada no mesmo request.
 */
public record DiretorDiretoriaRequestDTO(

        @NotNull(message = "ID do colaborador é obrigatório")
        Long colaboradorId,

        @NotNull(message = "ID da função é obrigatório")
        Long funcaoId,

        List<Permissao> permissoes
) {}
