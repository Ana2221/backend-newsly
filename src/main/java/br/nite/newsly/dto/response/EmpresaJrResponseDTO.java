package br.nite.newsly.dto.response;

import java.util.List;

public record EmpresaJrResponseDTO(
        Long id,
        String nome,
        String cnpj,
        String descricao,
        String logoUrl,
        ContatoResponseDTO contato,
        ConfigurationsResponseDTO configurations,
        ResponsavelResponseDTO responsavel,
        List<ColaboradorResponseDTO> colaboradores,
        List<DiretoriaResponseDTO> diretorias,
        List<NoticiaResponseDTO> noticias
) {}
