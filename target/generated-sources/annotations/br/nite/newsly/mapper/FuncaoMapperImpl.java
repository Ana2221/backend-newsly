package br.nite.newsly.mapper;

import br.nite.newsly.dto.request.FuncaoRequestDTO;
import br.nite.newsly.dto.response.FuncaoResponseDTO;
import br.nite.newsly.entity.Funcao;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-12T16:21:48-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 26 (Oracle Corporation)"
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

        funcao.setNome( dto.nome() );
        funcao.setDescricao( dto.descricao() );

        return funcao;
    }

    @Override
    public void updateEntity(FuncaoRequestDTO dto, Funcao entity) {
        if ( dto == null ) {
            return;
        }

        if ( dto.nome() != null ) {
            entity.setNome( dto.nome() );
        }
        if ( dto.descricao() != null ) {
            entity.setDescricao( dto.descricao() );
        }
    }
}
