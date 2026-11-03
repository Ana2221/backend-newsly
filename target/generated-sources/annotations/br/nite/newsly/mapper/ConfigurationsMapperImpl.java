package br.nite.newsly.mapper;

import br.nite.newsly.dto.request.ConfigurationsRequestDTO;
import br.nite.newsly.dto.response.ConfigurationsResponseDTO;
import br.nite.newsly.entity.Configurations;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-11-02T21:56:40-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.5 (Eclipse Adoptium)"
)
@Component
public class ConfigurationsMapperImpl implements ConfigurationsMapper {

    @Override
    public ConfigurationsResponseDTO toResponse(Configurations entity) {
        if ( entity == null ) {
            return null;
        }

        String color = null;

        color = entity.getColor();

        ConfigurationsResponseDTO configurationsResponseDTO = new ConfigurationsResponseDTO( color );

        return configurationsResponseDTO;
    }

    @Override
    public Configurations toEntity(ConfigurationsRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Configurations configurations = new Configurations();

        configurations.setColor( dto.color() );

        return configurations;
    }

    @Override
    public void updateEntity(ConfigurationsRequestDTO dto, Configurations entity) {
        if ( dto == null ) {
            return;
        }

        if ( dto.color() != null ) {
            entity.setColor( dto.color() );
        }
    }
}
