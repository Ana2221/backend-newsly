package br.nite.newsly.mapper;

import br.nite.newsly.dto.request.EmpresaJrRequestDTO;
import br.nite.newsly.dto.response.EmpresaJrResponseDTO;
import br.nite.newsly.entity.EmpresaJr;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", uses = {
        ColaboradorMapper.class,
        DiretoriaMapper.class,
        NoticiaMapper.class,
        ResponsavelMapper.class,
        ConfigurationsMapper.class,
        ContatoMapper.class
})
public interface EmpresaJrMapper {

    @Mapping(source = "colaboradores", target = "colaboradores")
    @Mapping(source = "diretorias", target = "diretorias")
    @Mapping(source = "noticias", target = "noticias")
    @Mapping(source = "responsavel", target = "responsavel")
    @Mapping(source = "configurations", target = "configurations")
    @Mapping(source = "contato", target = "contato")
    EmpresaJrResponseDTO toResponse(EmpresaJr entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "diretorias", ignore = true)
    @Mapping(target = "colaboradores", ignore = true)
    @Mapping(target = "noticias", ignore = true)
    @Mapping(target = "responsavel", ignore = true)
    @Mapping(target = "logoUrl", ignore = true)
    EmpresaJr toEntity(EmpresaJrRequestDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "diretorias", ignore = true)
    @Mapping(target = "colaboradores", ignore = true)
    @Mapping(target = "noticias", ignore = true)
    @Mapping(target = "responsavel", ignore = true)
    @Mapping(target = "logoUrl", ignore = true)
    void updateEntity(EmpresaJrRequestDTO dto, @MappingTarget EmpresaJr entity);
}
