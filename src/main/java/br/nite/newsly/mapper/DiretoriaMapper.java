package br.nite.newsly.mapper;

import br.nite.newsly.dto.request.DiretoriaRequestDTO;
import br.nite.newsly.dto.response.DiretoriaResponseDTO;
import br.nite.newsly.entity.Diretoria;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = { DiretorResumoMapper.class })
public interface DiretoriaMapper {

    @Mapping(source = "empresa.id", target = "empresaId")
    @Mapping(source = "empresa.nome", target = "empresaNome")
    @Mapping(source = "diretores", target = "diretores")
    DiretoriaResponseDTO toResponse(Diretoria entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "empresa", ignore = true)
    @Mapping(target = "diretores", ignore = true)
    Diretoria toEntity(DiretoriaRequestDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "empresa", ignore = true)
    @Mapping(target = "diretores", ignore = true)
    void updateEntity(DiretoriaRequestDTO dto, @MappingTarget Diretoria entity);
}
