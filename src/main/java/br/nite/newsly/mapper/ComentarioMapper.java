package br.nite.newsly.mapper;

import br.nite.newsly.dto.request.ComentarioRequestDTO;
import br.nite.newsly.dto.response.ComentarioResponseDTO;
import br.nite.newsly.entity.Comentario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ComentarioMapper {

    @Mapping(source = "noticia.id", target = "noticiaId")
    @Mapping(source = "comentarioPai.id", target = "comentarioPaiId")
    ComentarioResponseDTO toResponse(Comentario entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "noticia", ignore = true)
    @Mapping(target = "comentarioPai", ignore = true)
    @Mapping(target = "respostas", ignore = true)
    Comentario toEntity(ComentarioRequestDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "noticia", ignore = true)
    @Mapping(target = "comentarioPai", ignore = true)
    @Mapping(target = "respostas", ignore = true)
    void updateEntity(ComentarioRequestDTO dto, @MappingTarget Comentario entity);
}
