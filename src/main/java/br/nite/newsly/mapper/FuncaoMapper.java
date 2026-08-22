package br.nite.newsly.mapper;

import br.nite.newsly.dto.request.FuncaoRequestDTO;
import br.nite.newsly.dto.response.FuncaoResponseDTO;
import br.nite.newsly.entity.Funcao;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface FuncaoMapper {

    FuncaoResponseDTO toResponse(Funcao entity);

    @Mapping(target = "id", ignore = true)
    Funcao toEntity(FuncaoRequestDTO dto);

    @Mapping(target = "id", ignore = true)
    void updateEntity(FuncaoRequestDTO dto, @MappingTarget Funcao entity);
}
