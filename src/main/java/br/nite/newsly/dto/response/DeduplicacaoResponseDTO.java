package br.nite.newsly.dto.response;

import java.util.List;

/**
 * Sumário retornado após a operação de deduplicação de usuários.
 *
 * <ul>
 *   <li>{@code totalRemovidos} - quantos registros foram excluídos.</li>
 *   <li>{@code usuariosRemovidos} - detalhes de cada usuário excluído (o mais recente de cada grupo duplicado).</li>
 *   <li>{@code mensagem} - descrição legível do resultado.</li>
 * </ul>
 */
public record DeduplicacaoResponseDTO(
        int totalRemovidos,
        List<UsuarioRemovidoDTO> usuariosRemovidos,
        String mensagem
) {

    /**
     * Representa o usuário que foi removido (o mais recente de um grupo duplicado).
     *
     * @param id    id do usuário removido
     * @param nome  nome compartilhado pelo grupo
     * @param email e-mail do usuário removido
     * @param tipo  tipo discriminado (GESTOR, COLABORADOR, RESPONSAVEL, etc.)
     */
    public record UsuarioRemovidoDTO(
            Long id,
            String nome,
            String email,
            String tipo
    ) {}
}
