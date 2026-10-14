package br.nite.newsly.mapper;

import br.nite.newsly.dto.request.ConfigurationsRequestDTO;
import br.nite.newsly.dto.response.ConfigurationsResponseDTO;
import br.nite.newsly.entity.Configurations;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ConfigurationsMapper {

    ConfigurationsResponseDTO toResponse(Configurations entity);

    Configurations toEntity(ConfigurationsRequestDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(ConfigurationsRequestDTO dto, @MappingTarget Configurations entity);
}
