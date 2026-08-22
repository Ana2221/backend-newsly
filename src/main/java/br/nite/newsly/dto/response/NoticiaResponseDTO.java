package br.nite.newsly.dto.response;

import java.time.LocalDateTime;

public record NoticiaResponseDTO(
        Long id,
        String titulo,
        String conteudo,
        LocalDateTime publicadaEm,
        Long empresaId,
        String empresaNome
) {}
