package br.nite.newsly.mapper;

import br.nite.newsly.dto.response.DiretorResumoResponseDTO;
import br.nite.newsly.entity.Diretor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { FuncaoMapper.class })
public interface DiretorResumoMapper {

    @Mapping(source = "colaborador.id", target = "colaboradorId")
    @Mapping(source = "colaborador.nome", target = "colaboradorNome")
    DiretorResumoResponseDTO toResumo(Diretor entity);
}
