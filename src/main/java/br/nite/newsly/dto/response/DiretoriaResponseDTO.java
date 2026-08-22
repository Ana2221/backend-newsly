package br.nite.newsly.dto.response;

import java.time.LocalDate;
import java.util.List;

public record DiretoriaResponseDTO(
        Long id,
        LocalDate criadaEm,
        Long empresaId,
        String empresaNome,
        List<DiretorResumoResponseDTO> diretores
) {}
