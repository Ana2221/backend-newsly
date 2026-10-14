package br.nite.newsly.dto.response;

import br.nite.newsly.enums.StatusSolicitacao;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SolicitacaoResponseDTO(
        Long id,
        Long responsavelId,
        String responsavelNome,
        Long gestorId,
        String gestorNome,
        Long noticiaId,
        String noticiaTitulo,
        StatusSolicitacao status,
        LocalDateTime criadaEm,
        LocalDateTime respondidaEm,
        String motivoRecusa
) {}
