package br.nite.newsly.mapper;

import br.nite.newsly.dto.request.ContatoRequestDTO;
import br.nite.newsly.dto.response.ContatoResponseDTO;
import br.nite.newsly.entity.Contato;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ContatoMapper {

    ContatoResponseDTO toResponse(Contato entity);

    Contato toEntity(ContatoRequestDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(ContatoRequestDTO dto, @MappingTarget Contato entity);
}
