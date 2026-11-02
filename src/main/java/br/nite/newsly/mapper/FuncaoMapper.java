package br.nite.newsly.mapper;

import br.nite.newsly.dto.request.FuncaoRequestDTO;
import br.nite.newsly.dto.response.FuncaoResponseDTO;
import br.nite.newsly.entity.Funcao;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface FuncaoMapper {

    @Mapping(source = "empresa.id", target = "empresaId")
    FuncaoResponseDTO toResponse(Funcao entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "empresa", ignore = true)
    Funcao toEntity(FuncaoRequestDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "empresa", ignore = true)
    void updateEntity(FuncaoRequestDTO dto, @MappingTarget Funcao entity);
}
