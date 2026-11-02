package br.nite.newsly.mapper;

import br.nite.newsly.dto.response.DiretorResumoResponseDTO;
import br.nite.newsly.dto.response.FuncaoResponseDTO;
import br.nite.newsly.entity.Colaborador;
import br.nite.newsly.entity.Diretor;
import br.nite.newsly.enums.Permissao;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-11-02T17:53:48-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 26 (Oracle Corporation)"
)
@Component
public class DiretorResumoMapperImpl implements DiretorResumoMapper {

    @Autowired
    private FuncaoMapper funcaoMapper;

    @Override
    public DiretorResumoResponseDTO toResumo(Diretor entity) {
        if ( entity == null ) {
            return null;
        }

        Long colaboradorId = null;
        String colaboradorNome = null;
        Long id = null;
        FuncaoResponseDTO funcao = null;
        List<Permissao> permissoes = null;

        colaboradorId = entityColaboradorId( entity );
        colaboradorNome = entityColaboradorNome( entity );
        id = entity.getId();
        funcao = funcaoMapper.toResponse( entity.getFuncao() );
        List<Permissao> list = entity.getPermissoes();
        if ( list != null ) {
            permissoes = new ArrayList<Permissao>( list );
        }

        DiretorResumoResponseDTO diretorResumoResponseDTO = new DiretorResumoResponseDTO( id, colaboradorId, colaboradorNome, funcao, permissoes );

        return diretorResumoResponseDTO;
    }

    private Long entityColaboradorId(Diretor diretor) {
        Colaborador colaborador = diretor.getColaborador();
        if ( colaborador == null ) {
            return null;
        }
        return colaborador.getId();
    }

    private String entityColaboradorNome(Diretor diretor) {
        Colaborador colaborador = diretor.getColaborador();
        if ( colaborador == null ) {
            return null;
        }
        return colaborador.getNome();
    }
}
