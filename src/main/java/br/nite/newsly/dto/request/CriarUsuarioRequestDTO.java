package br.nite.newsly.dto.request;

import br.nite.newsly.enums.Permissao;
import br.nite.newsly.enums.TipoUsuario;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * DTO unificado para criação de qualquer tipo de usuário.
 *
 * O campo "tipo" determina qual entidade será criada:
 *   - USUARIO      → apenas Usuario base
 *   - COLABORADOR  → Colaborador (requer atividade)
 *   - GESTOR       → Gestor
 *   - RESPONSAVEL  → Responsavel
 *
 * Campos específicos de cada tipo são opcionais aqui e validados
 * no UsuarioAdapterService conforme o tipo informado.
 */
public record CriarUsuarioRequestDTO(

        @NotNull(message = "Tipo de usuário é obrigatório")
        TipoUsuario tipo,

        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 150, message = "Nome deve ter no máximo 150 caracteres")
        String nome,

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 6, max = 255, message = "Senha deve ter entre 6 e 255 caracteres")
        String senha,

        @Valid
        ContatoRequestDTO contato,

        // Colaborador
        Boolean atividade,

        // Colaborador e Responsavel
        Long empresaId,

        // Gestor, Responsavel
        List<Permissao> permissoes
) {}
