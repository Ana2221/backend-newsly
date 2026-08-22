package br.nite.newsly.mapper;

import br.nite.newsly.dto.request.ContatoRequestDTO;
import br.nite.newsly.dto.response.ContatoResponseDTO;
import br.nite.newsly.entity.Contato;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ContatoMapper {

    ContatoResponseDTO toResponse(Contato entity);

    Contato toEntity(ContatoRequestDTO dto);

    void updateEntity(ContatoRequestDTO dto, @MappingTarget Contato entity);
}
