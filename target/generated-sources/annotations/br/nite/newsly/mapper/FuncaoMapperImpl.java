package br.nite.newsly.mapper;

import br.nite.newsly.dto.request.FuncaoRequestDTO;
import br.nite.newsly.dto.response.FuncaoResponseDTO;
import br.nite.newsly.entity.EmpresaJr;
import br.nite.newsly.entity.Funcao;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-11-12T19:08:08-0300",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.200.v20261008-1115, environment: Java 25.0.4.1 (Eclipse Adoptium)"
)
@Component
public class FuncaoMapperImpl implements FuncaoMapper {

    @Override
    public FuncaoResponseDTO toResponse(Funcao entity) {
        if ( entity == null ) {
            return null;
        }

        Long empresaId = null;
        Long id = null;
        String nome = null;
        String descricao = null;

        empresaId = entityEmpresaId( entity );
        id = entity.getId();
        nome = entity.getNome();
        descricao = entity.getDescricao();

        FuncaoResponseDTO funcaoResponseDTO = new FuncaoResponseDTO( id, nome, descricao, empresaId );

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

        if ( dto.descricao() != null ) {
            entity.setDescricao( dto.descricao() );
        }
        if ( dto.nome() != null ) {
            entity.setNome( dto.nome() );
        }
    }

    private Long entityEmpresaId(Funcao funcao) {
        EmpresaJr empresa = funcao.getEmpresa();
        if ( empresa == null ) {
            return null;
        }
        return empresa.getId();
    }
}
