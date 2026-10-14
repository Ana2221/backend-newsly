package br.nite.newsly.mapper;

import br.nite.newsly.dto.request.UsuarioRequestDTO;
import br.nite.newsly.dto.response.UsuarioResponseDTO;
import br.nite.newsly.entity.Usuario;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", uses = { ContatoMapper.class })
public interface UsuarioMapper {

    UsuarioResponseDTO toResponse(Usuario entity);

    Usuario toEntity(UsuarioRequestDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(UsuarioRequestDTO dto, @MappingTarget Usuario entity);
}
