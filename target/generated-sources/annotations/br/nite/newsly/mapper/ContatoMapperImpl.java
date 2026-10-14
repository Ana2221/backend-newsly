package br.nite.newsly.mapper;

import br.nite.newsly.dto.request.ContatoRequestDTO;
import br.nite.newsly.dto.response.ContatoResponseDTO;
import br.nite.newsly.entity.Contato;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-12T16:21:47-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 26 (Oracle Corporation)"
)
@Component
public class ContatoMapperImpl implements ContatoMapper {

    @Override
    public ContatoResponseDTO toResponse(Contato entity) {
        if ( entity == null ) {
            return null;
        }

        String telefone = null;
        String email = null;

        telefone = entity.getTelefone();
        email = entity.getEmail();

        ContatoResponseDTO contatoResponseDTO = new ContatoResponseDTO( telefone, email );

        return contatoResponseDTO;
    }

    @Override
    public Contato toEntity(ContatoRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Contato contato = new Contato();

        contato.setTelefone( dto.telefone() );
        contato.setEmail( dto.email() );

        return contato;
    }

    @Override
    public void updateEntity(ContatoRequestDTO dto, Contato entity) {
        if ( dto == null ) {
            return;
        }

        if ( dto.telefone() != null ) {
            entity.setTelefone( dto.telefone() );
        }
        if ( dto.email() != null ) {
            entity.setEmail( dto.email() );
        }
    }
}
