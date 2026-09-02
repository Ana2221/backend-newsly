package br.nite.newsly.mapper;

import br.nite.newsly.dto.request.ComentarioRequestDTO;
import br.nite.newsly.dto.response.ComentarioResponseDTO;
import br.nite.newsly.entity.Comentario;
import br.nite.newsly.entity.Noticia;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-31T10:07:21-0300",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.100.v20260624-0231, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class ComentarioMapperImpl implements ComentarioMapper {

    @Override
    public ComentarioResponseDTO toResponse(Comentario entity) {
        if ( entity == null ) {
            return null;
        }

        Long noticiaId = null;
        Long comentarioPaiId = null;
        Long id = null;
        String conteudo = null;
        LocalDateTime publicadoEm = null;
        List<ComentarioResponseDTO> respostas = null;

        noticiaId = entityNoticiaId( entity );
        comentarioPaiId = entityComentarioPaiId( entity );
        id = entity.getId();
        conteudo = entity.getConteudo();
        publicadoEm = entity.getPublicadoEm();
        respostas = comentarioListToComentarioResponseDTOList( entity.getRespostas() );

        ComentarioResponseDTO comentarioResponseDTO = new ComentarioResponseDTO( id, conteudo, publicadoEm, noticiaId, comentarioPaiId, respostas );

        return comentarioResponseDTO;
    }

    @Override
    public Comentario toEntity(ComentarioRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Comentario comentario = new Comentario();

        comentario.setConteudo( dto.conteudo() );
        comentario.setPublicadoEm( dto.publicadoEm() );

        return comentario;
    }

    @Override
    public void updateEntity(ComentarioRequestDTO dto, Comentario entity) {
        if ( dto == null ) {
            return;
        }

        entity.setConteudo( dto.conteudo() );
        entity.setPublicadoEm( dto.publicadoEm() );
    }

    private Long entityNoticiaId(Comentario comentario) {
        Noticia noticia = comentario.getNoticia();
        if ( noticia == null ) {
            return null;
        }
        return noticia.getId();
    }

    private Long entityComentarioPaiId(Comentario comentario) {
        Comentario comentarioPai = comentario.getComentarioPai();
        if ( comentarioPai == null ) {
            return null;
        }
        return comentarioPai.getId();
    }

    protected List<ComentarioResponseDTO> comentarioListToComentarioResponseDTOList(List<Comentario> list) {
        if ( list == null ) {
            return null;
        }

        List<ComentarioResponseDTO> list1 = new ArrayList<ComentarioResponseDTO>( list.size() );
        for ( Comentario comentario : list ) {
            list1.add( toResponse( comentario ) );
        }

        return list1;
    }
}
