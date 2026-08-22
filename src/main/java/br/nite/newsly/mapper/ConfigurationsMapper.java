package br.nite.newsly.mapper;


import org.mapstruct.Mapper;

import br.nite.newsly.dto.response.ConfigurationsResponseDTO;
import br.nite.newsly.entity.Configurations;

@Mapper(componentModel = "spring")
public interface ConfigurationsMapper {

    ConfigurationsResponseDTO toResponse(Configurations entity);
}
