package br.nite.newsly.mapper;

import br.nite.newsly.dto.request.ContatoRequestDTO;
import br.nite.newsly.dto.response.ContatoResponseDTO;
import br.nite.newsly.entity.Contato;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-11-12T19:08:12-0300",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.200.v20261008-1115, environment: Java 25.0.4.1 (Eclipse Adoptium)"
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

        contato.setEmail( dto.email() );
        contato.setTelefone( dto.telefone() );

        return contato;
    }

    @Override
    public void updateEntity(ContatoRequestDTO dto, Contato entity) {
        if ( dto == null ) {
            return;
        }

        if ( dto.email() != null ) {
            entity.setEmail( dto.email() );
        }
        if ( dto.telefone() != null ) {
            entity.setTelefone( dto.telefone() );
        }
    }
}
