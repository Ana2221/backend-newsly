package br.nite.newsly.mapper;

import br.nite.newsly.dto.request.GestorRequestDTO;
import br.nite.newsly.dto.response.ContatoResponseDTO;
import br.nite.newsly.dto.response.GestorResponseDTO;
import br.nite.newsly.entity.Contato;
import br.nite.newsly.entity.Gestor;
import br.nite.newsly.enums.Permissao;
import br.nite.newsly.enums.TipoUsuario;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-31T10:07:20-0300",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.100.v20260624-0231, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class GestorMapperImpl implements GestorMapper {

    @Autowired
    private ContatoMapper contatoMapper;

    @Override
    public GestorResponseDTO toResponse(Gestor entity) {
        if ( entity == null ) {
            return null;
        }

        Long id = null;
        UUID matricula = null;
        String nome = null;
        ContatoResponseDTO contato = null;
        List<Permissao> permissoes = null;

        id = entity.getId();
        matricula = entity.getMatricula();
        nome = entity.getNome();
        contato = contatoMapper.toResponse( entity.getContato() );
        List<Permissao> list = entity.getPermissoes();
        if ( list != null ) {
            permissoes = new ArrayList<Permissao>( list );
        }

        TipoUsuario tipo = TipoUsuario.GESTOR;

        GestorResponseDTO gestorResponseDTO = new GestorResponseDTO( id, matricula, nome, contato, permissoes, tipo );

        return gestorResponseDTO;
    }

    @Override
    public Gestor toEntity(GestorRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Gestor gestor = new Gestor();

        gestor.setContato( contatoMapper.toEntity( dto.contato() ) );
        gestor.setNome( dto.nome() );
        gestor.setSenha( dto.senha() );
        List<Permissao> list = dto.permissoes();
        if ( list != null ) {
            gestor.setPermissoes( new ArrayList<Permissao>( list ) );
        }

        gestor.setMatricula( UUID.randomUUID() );

        return gestor;
    }

    @Override
    public void updateEntity(GestorRequestDTO dto, Gestor entity) {
        if ( dto == null ) {
            return;
        }

        if ( dto.contato() != null ) {
            if ( entity.getContato() == null ) {
                entity.setContato( new Contato() );
            }
            contatoMapper.updateEntity( dto.contato(), entity.getContato() );
        }
        else {
            entity.setContato( null );
        }
        entity.setNome( dto.nome() );
        entity.setSenha( dto.senha() );
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
}
