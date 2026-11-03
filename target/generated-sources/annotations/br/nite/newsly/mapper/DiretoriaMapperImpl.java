package br.nite.newsly.mapper;

import br.nite.newsly.dto.request.DiretoriaRequestDTO;
import br.nite.newsly.dto.response.DiretorResumoResponseDTO;
import br.nite.newsly.dto.response.DiretoriaResponseDTO;
import br.nite.newsly.entity.Diretor;
import br.nite.newsly.entity.Diretoria;
import br.nite.newsly.entity.EmpresaJr;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-11-02T21:56:40-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.5 (Eclipse Adoptium)"
)
@Component
public class DiretoriaMapperImpl implements DiretoriaMapper {

    @Autowired
    private DiretorResumoMapper diretorResumoMapper;

    @Override
    public DiretoriaResponseDTO toResponse(Diretoria entity) {
        if ( entity == null ) {
            return null;
        }

        Long empresaId = null;
        String empresaNome = null;
        List<DiretorResumoResponseDTO> diretores = null;
        Long id = null;
        String nome = null;
        LocalDate criadaEm = null;

        empresaId = entityEmpresaId( entity );
        empresaNome = entityEmpresaNome( entity );
        diretores = diretorListToDiretorResumoResponseDTOList( entity.getDiretores() );
        id = entity.getId();
        nome = entity.getNome();
        criadaEm = entity.getCriadaEm();

        DiretoriaResponseDTO diretoriaResponseDTO = new DiretoriaResponseDTO( id, nome, criadaEm, empresaId, empresaNome, diretores );

        return diretoriaResponseDTO;
    }

    @Override
    public Diretoria toEntity(DiretoriaRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Diretoria diretoria = new Diretoria();

        diretoria.setNome( dto.nome() );
        diretoria.setCriadaEm( dto.criadaEm() );

        return diretoria;
    }

    @Override
    public void updateEntity(DiretoriaRequestDTO dto, Diretoria entity) {
        if ( dto == null ) {
            return;
        }

        entity.setNome( dto.nome() );
        entity.setCriadaEm( dto.criadaEm() );
    }

    private Long entityEmpresaId(Diretoria diretoria) {
        EmpresaJr empresa = diretoria.getEmpresa();
        if ( empresa == null ) {
            return null;
        }
        return empresa.getId();
    }

    private String entityEmpresaNome(Diretoria diretoria) {
        EmpresaJr empresa = diretoria.getEmpresa();
        if ( empresa == null ) {
            return null;
        }
        return empresa.getNome();
    }

    protected List<DiretorResumoResponseDTO> diretorListToDiretorResumoResponseDTOList(List<Diretor> list) {
        if ( list == null ) {
            return null;
        }

        List<DiretorResumoResponseDTO> list1 = new ArrayList<DiretorResumoResponseDTO>( list.size() );
        for ( Diretor diretor : list ) {
            list1.add( diretorResumoMapper.toResumo( diretor ) );
        }

        return list1;
    }
}
