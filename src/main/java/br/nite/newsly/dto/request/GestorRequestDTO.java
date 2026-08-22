package br.nite.newsly.dto.request;

import br.nite.newsly.enums.Permissao;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record GestorRequestDTO(

        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 150, message = "Nome deve ter no máximo 150 caracteres")
        String nome,

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 6, max = 255, message = "Senha deve ter entre 6 e 255 caracteres")
        String senha,

        @Valid
        ContatoRequestDTO contato,

        List<Permissao> permissoes
) {}
