package br.nite.newsly.dto.response;

import br.nite.newsly.enums.Permissao;

import java.util.List;

public record DiretorResponseDTO(
        Long id,
        Long colaboradorId,
        String colaboradorNome,
        FuncaoResponseDTO funcao,
        DiretoriaResponseDTO diretoria,
        List<Permissao> permissoes
) {}
