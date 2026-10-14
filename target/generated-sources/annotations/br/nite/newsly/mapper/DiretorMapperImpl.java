package br.nite.newsly.mapper;

import br.nite.newsly.dto.request.DiretorRequestDTO;
import br.nite.newsly.dto.response.DiretorResponseDTO;
import br.nite.newsly.dto.response.DiretoriaResponseDTO;
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
    date = "2026-10-12T16:21:45-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 26 (Oracle Corporation)"
)
@Component
public class DiretorMapperImpl implements DiretorMapper {

    @Autowired
    private FuncaoMapper funcaoMapper;
    @Autowired
    private DiretoriaMapper diretoriaMapper;

    @Override
    public DiretorResponseDTO toResponse(Diretor entity) {
        if ( entity == null ) {
            return null;
        }

        Long colaboradorId = null;
        String colaboradorNome = null;
        Long id = null;
        FuncaoResponseDTO funcao = null;
        DiretoriaResponseDTO diretoria = null;
        List<Permissao> permissoes = null;

        colaboradorId = entityColaboradorId( entity );
        colaboradorNome = entityColaboradorNome( entity );
        id = entity.getId();
        funcao = funcaoMapper.toResponse( entity.getFuncao() );
        diretoria = diretoriaMapper.toResponse( entity.getDiretoria() );
        List<Permissao> list = entity.getPermissoes();
        if ( list != null ) {
            permissoes = new ArrayList<Permissao>( list );
        }

        DiretorResponseDTO diretorResponseDTO = new DiretorResponseDTO( id, colaboradorId, colaboradorNome, funcao, diretoria, permissoes );

        return diretorResponseDTO;
    }

    @Override
    public Diretor toEntity(DiretorRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Diretor diretor = new Diretor();

        List<Permissao> list = dto.permissoes();
        if ( list != null ) {
            diretor.setPermissoes( new ArrayList<Permissao>( list ) );
        }

        return diretor;
    }

    @Override
    public void updateEntity(DiretorRequestDTO dto, Diretor entity) {
        if ( dto == null ) {
            return;
        }

        if ( entity.getPermissoes() != null ) {
            List<Permissao> list = dto.permissoes();
            if ( list != null ) {
                entity.getPermissoes().clear();
                entity.getPermissoes().addAll( list );
            }
            else {
                entity.setPermissoes( null );
            }
        }
        else {
            List<Permissao> list = dto.permissoes();
            if ( list != null ) {
                entity.setPermissoes( new ArrayList<Permissao>( list ) );
            }
        }
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
