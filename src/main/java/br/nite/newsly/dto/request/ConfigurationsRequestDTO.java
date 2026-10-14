package br.nite.newsly.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ConfigurationsRequestDTO(

        @Size(max = 20, message = "Cor deve ter no máximo 20 caracteres")
        @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$",
                 message = "Cor deve ser um hexadecimal válido (ex: #FF5733)")
        String color
) {}
