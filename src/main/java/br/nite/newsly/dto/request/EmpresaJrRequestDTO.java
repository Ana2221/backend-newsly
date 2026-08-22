package br.nite.newsly.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EmpresaJrRequestDTO(

        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 150, message = "Nome deve ter no máximo 150 caracteres")
        String nome,

        @NotBlank(message = "CNPJ é obrigatório")
        @Pattern(regexp = "\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}",
                 message = "CNPJ deve seguir o formato XX.XXX.XXX/XXXX-XX")
        String cnpj,

        ContatoRequestDTO contato,

        @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
        String descricao,

        ConfigurationsRequestDTO configurations,

        

        Long responsavelId
) {}
