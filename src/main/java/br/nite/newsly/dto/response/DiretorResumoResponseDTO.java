package br.nite.newsly.dto.response;

import br.nite.newsly.enums.Permissao;

import java.util.List;

/**
 * Versão resumida do DiretorResponseDTO usada dentro do DiretoriaResponseDTO
 * para evitar recursão circular (DiretorResponseDTO → DiretoriaResponseDTO → DiretorResponseDTO).
 */
public record DiretorResumoResponseDTO(
        Long id,
        Long colaboradorId,
        String colaboradorNome,
        FuncaoResponseDTO funcao,
        List<Permissao> permissoes
) {}
