package br.nite.newsly.mapper;

import br.nite.newsly.dto.request.ContatoRequestDTO;
import br.nite.newsly.dto.response.ContatoResponseDTO;
import br.nite.newsly.entity.Contato;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-19T17:39:38-0300",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.100.v20260624-0231, environment: Java 21.0.11 (Eclipse Adoptium)"
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

        entity.setEmail( dto.email() );
        entity.setTelefone( dto.telefone() );
    }
}
