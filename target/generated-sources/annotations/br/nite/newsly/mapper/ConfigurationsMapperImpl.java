package br.nite.newsly.mapper;

import br.nite.newsly.dto.response.ConfigurationsResponseDTO;
import br.nite.newsly.entity.Configurations;
import java.util.Arrays;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-31T10:07:19-0300",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.100.v20260624-0231, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class ConfigurationsMapperImpl implements ConfigurationsMapper {

    @Override
    public ConfigurationsResponseDTO toResponse(Configurations entity) {
        if ( entity == null ) {
            return null;
        }

        String color = null;
        byte[] logo = null;

        color = entity.getColor();
        byte[] logo1 = entity.getLogo();
        if ( logo1 != null ) {
            logo = Arrays.copyOf( logo1, logo1.length );
        }

        ConfigurationsResponseDTO configurationsResponseDTO = new ConfigurationsResponseDTO( color, logo );

        return configurationsResponseDTO;
    }
}
