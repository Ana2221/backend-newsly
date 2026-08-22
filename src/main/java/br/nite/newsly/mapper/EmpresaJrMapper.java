package br.nite.newsly.mapper;

import br.nite.newsly.dto.request.EmpresaJrRequestDTO;
import br.nite.newsly.dto.response.EmpresaJrResponseDTO;
import br.nite.newsly.entity.EmpresaJr;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = { ColaboradorMapper.class, DiretoriaMapper.class, NoticiaMapper.class ,ContatoMapper.class, ConfigurationsMapper.class})
public interface EmpresaJrMapper {

    @Mapping(source = "colaboradores", target = "colaboradores")
    @Mapping(source = "diretorias", target = "diretorias")
    @Mapping(source = "noticias", target = "noticias")
    EmpresaJrResponseDTO toResponse(EmpresaJr entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "diretorias", ignore = true)
    @Mapping(target = "colaboradores", ignore = true)
    @Mapping(target = "noticias", ignore = true)
    EmpresaJr toEntity(EmpresaJrRequestDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "diretorias", ignore = true)
    @Mapping(target = "colaboradores", ignore = true)
    @Mapping(target = "noticias", ignore = true)
    void updateEntity(EmpresaJrRequestDTO dto, @MappingTarget EmpresaJr entity);
}
