package br.nite.newsly.dto.response;

import java.time.LocalDateTime;

public record NoticiaResponseDTO(
        Long id,
        String titulo,
        String conteudo,
        LocalDateTime publicadaEm,
        String imagemUrl,
        Long empresaId,
        String empresaNome
) {}
