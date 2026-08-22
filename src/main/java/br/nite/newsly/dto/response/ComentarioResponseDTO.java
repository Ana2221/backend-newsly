package br.nite.newsly.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record ComentarioResponseDTO(
        Long id,
        String conteudo,
        LocalDateTime publicadoEm,
        Long noticiaId,
        Long comentarioPaiId,
        List<ComentarioResponseDTO> respostas
) {}
