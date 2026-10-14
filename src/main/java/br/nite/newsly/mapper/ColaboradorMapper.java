package br.nite.newsly.mapper;

import br.nite.newsly.dto.request.ColaboradorRequestDTO;
import br.nite.newsly.dto.response.ColaboradorResponseDTO;
import br.nite.newsly.entity.Colaborador;
import br.nite.newsly.enums.TipoUsuario;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.UUID;

@Mapper(componentModel = "spring", uses = { ContatoMapper.class }, imports = { TipoUsuario.class, UUID.class })
public interface ColaboradorMapper {

    @Mapping(source = "empresa.id", target = "empresaId")
    @Mapping(target = "tipo", expression = "java(TipoUsuario.COLABORADOR)")
    ColaboradorResponseDTO toResponse(Colaborador entity);

    @Mapping(target = "matricula", expression = "java(UUID.randomUUID())")
    @Mapping(target = "empresa", ignore = true)
    @Mapping(target = "id", ignore = true)
    Colaborador toEntity(ColaboradorRequestDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "matricula", ignore = true)
    @Mapping(target = "empresa", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateEntity(ColaboradorRequestDTO dto, @MappingTarget Colaborador entity);
}
