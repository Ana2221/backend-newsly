package br.nite.newsly.mapper;

import br.nite.newsly.dto.request.NoticiaRequestDTO;
import br.nite.newsly.dto.response.NoticiaResponseDTO;
import br.nite.newsly.entity.EmpresaJr;
import br.nite.newsly.entity.Noticia;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-19T17:39:38-0300",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.100.v20260624-0231, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class NoticiaMapperImpl implements NoticiaMapper {

    @Override
    public NoticiaResponseDTO toResponse(Noticia entity) {
        if ( entity == null ) {
            return null;
        }

        Long empresaId = null;
        String empresaNome = null;
        Long id = null;
        String titulo = null;
        String conteudo = null;
        LocalDateTime publicadaEm = null;

        empresaId = entityEmpresaId( entity );
        empresaNome = entityEmpresaNome( entity );
        id = entity.getId();
        titulo = entity.getTitulo();
        conteudo = entity.getConteudo();
        publicadaEm = entity.getPublicadaEm();

        NoticiaResponseDTO noticiaResponseDTO = new NoticiaResponseDTO( id, titulo, conteudo, publicadaEm, empresaId, empresaNome );

        return noticiaResponseDTO;
    }

    @Override
    public Noticia toEntity(NoticiaRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Noticia noticia = new Noticia();

        noticia.setConteudo( dto.conteudo() );
        noticia.setPublicadaEm( dto.publicadaEm() );
        noticia.setTitulo( dto.titulo() );

        return noticia;
    }

    @Override
    public void updateEntity(NoticiaRequestDTO dto, Noticia entity) {
        if ( dto == null ) {
            return;
        }

        entity.setConteudo( dto.conteudo() );
        entity.setPublicadaEm( dto.publicadaEm() );
        entity.setTitulo( dto.titulo() );
    }

    private Long entityEmpresaId(Noticia noticia) {
        EmpresaJr empresa = noticia.getEmpresa();
        if ( empresa == null ) {
            return null;
        }
        return empresa.getId();
    }

    private String entityEmpresaNome(Noticia noticia) {
        EmpresaJr empresa = noticia.getEmpresa();
        if ( empresa == null ) {
            return null;
        }
        return empresa.getNome();
    }
}
