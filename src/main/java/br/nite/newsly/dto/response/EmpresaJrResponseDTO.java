package br.nite.newsly.dto.response;

import java.util.List;

public record EmpresaJrResponseDTO(
        Long id,
        String nome,
        String cnpj,
        List<ColaboradorResponseDTO> colaboradores,
        List<DiretoriaResponseDTO> diretorias,
        List<NoticiaResponseDTO> noticias,
        ContatoResponseDTO contato,
        ConfigurationsResponseDTO configurations,
        String descricao
) {}
