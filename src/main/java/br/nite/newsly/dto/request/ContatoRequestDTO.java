package br.nite.newsly.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record ContatoRequestDTO(

        @Size(max = 20, message = "Telefone deve ter no máximo 20 caracteres")
        String telefone,

        @Email(message = "E-mail inválido")
        @Size(max = 150, message = "E-mail deve ter no máximo 150 caracteres")
        String email
) {}
