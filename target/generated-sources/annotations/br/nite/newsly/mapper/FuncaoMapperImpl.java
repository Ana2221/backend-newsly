package br.nite.newsly.mapper;

import br.nite.newsly.dto.request.FuncaoRequestDTO;
import br.nite.newsly.dto.response.FuncaoResponseDTO;
import br.nite.newsly.entity.Funcao;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-19T17:39:37-0300",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.100.v20260624-0231, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class FuncaoMapperImpl implements FuncaoMapper {

    @Override
    public FuncaoResponseDTO toResponse(Funcao entity) {
        if ( entity == null ) {
            return null;
        }

        Long id = null;
        String nome = null;
        String descricao = null;

        id = entity.getId();
        nome = entity.getNome();
        descricao = entity.getDescricao();

        FuncaoResponseDTO funcaoResponseDTO = new FuncaoResponseDTO( id, nome, descricao );

        return funcaoResponseDTO;
    }

    @Override
    public Funcao toEntity(FuncaoRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Funcao funcao = new Funcao();

        funcao.setDescricao( dto.descricao() );
        funcao.setNome( dto.nome() );

        return funcao;
    }

    @Override
    public void updateEntity(FuncaoRequestDTO dto, Funcao entity) {
        if ( dto == null ) {
            return;
        }

        entity.setDescricao( dto.descricao() );
        entity.setNome( dto.nome() );
    }
}
