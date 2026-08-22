package br.nite.newsly.mapper;

import br.nite.newsly.dto.request.DiretorRequestDTO;
import br.nite.newsly.dto.response.DiretorResponseDTO;
import br.nite.newsly.entity.Diretor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = { FuncaoMapper.class, DiretoriaMapper.class })
public interface DiretorMapper {

    @Mapping(source = "colaborador.id", target = "colaboradorId")
    @Mapping(source = "colaborador.nome", target = "colaboradorNome")
    DiretorResponseDTO toResponse(Diretor entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "colaborador", ignore = true)
    @Mapping(target = "funcao", ignore = true)
    @Mapping(target = "diretoria", ignore = true)
    Diretor toEntity(DiretorRequestDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "colaborador", ignore = true)
    @Mapping(target = "funcao", ignore = true)
    @Mapping(target = "diretoria", ignore = true)
    void updateEntity(DiretorRequestDTO dto, @MappingTarget Diretor entity);
}
