package br.nite.newsly.mapper;

import br.nite.newsly.dto.request.GestorRequestDTO;
import br.nite.newsly.dto.response.GestorResponseDTO;
import br.nite.newsly.entity.Gestor;
import br.nite.newsly.enums.TipoUsuario;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.UUID;

@Mapper(componentModel = "spring", uses = { ContatoMapper.class }, imports = { TipoUsuario.class, UUID.class })
public interface GestorMapper {

    @Mapping(target = "tipo", expression = "java(TipoUsuario.GESTOR)")
    GestorResponseDTO toResponse(Gestor entity);

    @Mapping(target = "matricula", expression = "java(UUID.randomUUID())")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "administrador", ignore = true)
    Gestor toEntity(GestorRequestDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "matricula", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "administrador", ignore = true)
    void updateEntity(GestorRequestDTO dto, @MappingTarget Gestor entity);
}
