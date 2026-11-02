package br.nite.newsly.mapper;

import br.nite.newsly.dto.request.UsuarioRequestDTO;
import br.nite.newsly.dto.response.ContatoResponseDTO;
import br.nite.newsly.dto.response.UsuarioResponseDTO;
import br.nite.newsly.entity.Contato;
import br.nite.newsly.entity.Usuario;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-11-02T17:53:44-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 26 (Oracle Corporation)"
)
@Component
public class UsuarioMapperImpl implements UsuarioMapper {

    @Autowired
    private ContatoMapper contatoMapper;

    @Override
    public UsuarioResponseDTO toResponse(Usuario entity) {
        if ( entity == null ) {
            return null;
        }

        Long id = null;
        String nome = null;
        ContatoResponseDTO contato = null;

        id = entity.getId();
        nome = entity.getNome();
        contato = contatoMapper.toResponse( entity.getContato() );

        UsuarioResponseDTO usuarioResponseDTO = new UsuarioResponseDTO( id, nome, contato );

        return usuarioResponseDTO;
    }

    @Override
    public Usuario toEntity(UsuarioRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Usuario usuario = new Usuario();

        usuario.setNome( dto.nome() );
        usuario.setSenha( dto.senha() );
        usuario.setContato( contatoMapper.toEntity( dto.contato() ) );

        return usuario;
    }

    @Override
    public void updateEntity(UsuarioRequestDTO dto, Usuario entity) {
        if ( dto == null ) {
            return;
        }

        if ( dto.nome() != null ) {
            entity.setNome( dto.nome() );
        }
        if ( dto.senha() != null ) {
            entity.setSenha( dto.senha() );
        }
        if ( dto.contato() != null ) {
            if ( entity.getContato() == null ) {
                entity.setContato( new Contato() );
            }
            contatoMapper.updateEntity( dto.contato(), entity.getContato() );
        }
    }
}
