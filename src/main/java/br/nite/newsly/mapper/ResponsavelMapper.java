package br.nite.newsly.mapper;

import br.nite.newsly.dto.request.ResponsavelRequestDTO;
import br.nite.newsly.dto.response.ResponsavelResponseDTO;
import br.nite.newsly.entity.Responsavel;
import br.nite.newsly.enums.TipoUsuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.UUID;

@Mapper(componentModel = "spring", uses = { ContatoMapper.class }, imports = { TipoUsuario.class, UUID.class })
public interface ResponsavelMapper {

    @Mapping(source = "empresa.id", target = "empresaId")
    @Mapping(target = "tipo", expression = "java(TipoUsuario.RESPONSAVEL)")
    ResponsavelResponseDTO toResponse(Responsavel entity);

    @Mapping(target = "matricula", expression = "java(UUID.randomUUID())")
    @Mapping(target = "empresa", ignore = true)
    @Mapping(target = "id", ignore = true)
    Responsavel toEntity(ResponsavelRequestDTO dto);

    @Mapping(target = "matricula", ignore = true)
    @Mapping(target = "empresa", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateEntity(ResponsavelRequestDTO dto, @MappingTarget Responsavel entity);
}
