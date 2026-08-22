package br.nite.newsly.mapper;

import br.nite.newsly.dto.request.ResponsavelRequestDTO;
import br.nite.newsly.dto.response.ContatoResponseDTO;
import br.nite.newsly.dto.response.ResponsavelResponseDTO;
import br.nite.newsly.entity.Contato;
import br.nite.newsly.entity.EmpresaJr;
import br.nite.newsly.entity.Responsavel;
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
    date = "2026-08-19T17:39:37-0300",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.100.v20260624-0231, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class ResponsavelMapperImpl implements ResponsavelMapper {

    @Autowired
    private ContatoMapper contatoMapper;

    @Override
    public ResponsavelResponseDTO toResponse(Responsavel entity) {
        if ( entity == null ) {
            return null;
        }

        Long empresaId = null;
        Long id = null;
        UUID matricula = null;
        String nome = null;
        ContatoResponseDTO contato = null;
        List<Permissao> permissoes = null;

        empresaId = entityEmpresaId( entity );
        id = entity.getId();
        matricula = entity.getMatricula();
        nome = entity.getNome();
        contato = contatoMapper.toResponse( entity.getContato() );
        List<Permissao> list = entity.getPermissoes();
        if ( list != null ) {
            permissoes = new ArrayList<Permissao>( list );
        }

        TipoUsuario tipo = TipoUsuario.RESPONSAVEL;

        ResponsavelResponseDTO responsavelResponseDTO = new ResponsavelResponseDTO( id, matricula, nome, contato, permissoes, empresaId, tipo );

        return responsavelResponseDTO;
    }

    @Override
    public Responsavel toEntity(ResponsavelRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Responsavel responsavel = new Responsavel();

        responsavel.setContato( contatoMapper.toEntity( dto.contato() ) );
        responsavel.setNome( dto.nome() );
        responsavel.setSenha( dto.senha() );
        List<Permissao> list = dto.permissoes();
        if ( list != null ) {
            responsavel.setPermissoes( new ArrayList<Permissao>( list ) );
        }

        responsavel.setMatricula( UUID.randomUUID() );

        return responsavel;
    }

    @Override
    public void updateEntity(ResponsavelRequestDTO dto, Responsavel entity) {
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

    private Long entityEmpresaId(Responsavel responsavel) {
        EmpresaJr empresa = responsavel.getEmpresa();
        if ( empresa == null ) {
            return null;
        }
        return empresa.getId();
    }
}
