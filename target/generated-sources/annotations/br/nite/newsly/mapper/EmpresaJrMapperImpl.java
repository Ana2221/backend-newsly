package br.nite.newsly.mapper;

import br.nite.newsly.dto.request.EmpresaJrRequestDTO;
import br.nite.newsly.dto.response.ColaboradorResponseDTO;
import br.nite.newsly.dto.response.ConfigurationsResponseDTO;
import br.nite.newsly.dto.response.ContatoResponseDTO;
import br.nite.newsly.dto.response.DiretoriaResponseDTO;
import br.nite.newsly.dto.response.EmpresaJrResponseDTO;
import br.nite.newsly.dto.response.NoticiaResponseDTO;
import br.nite.newsly.dto.response.ResponsavelResponseDTO;
import br.nite.newsly.entity.Colaborador;
import br.nite.newsly.entity.Configurations;
import br.nite.newsly.entity.Contato;
import br.nite.newsly.entity.Diretoria;
import br.nite.newsly.entity.EmpresaJr;
import br.nite.newsly.entity.Noticia;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-11-12T20:22:27-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.5 (Eclipse Adoptium)"
)
@Component
public class EmpresaJrMapperImpl implements EmpresaJrMapper {

    @Autowired
    private ColaboradorMapper colaboradorMapper;
    @Autowired
    private DiretoriaMapper diretoriaMapper;
    @Autowired
    private NoticiaMapper noticiaMapper;
    @Autowired
    private ResponsavelMapper responsavelMapper;
    @Autowired
    private ConfigurationsMapper configurationsMapper;
    @Autowired
    private ContatoMapper contatoMapper;

    @Override
    public EmpresaJrResponseDTO toResponse(EmpresaJr entity) {
        if ( entity == null ) {
            return null;
        }

        List<ColaboradorResponseDTO> colaboradores = null;
        List<DiretoriaResponseDTO> diretorias = null;
        List<NoticiaResponseDTO> noticias = null;
        ResponsavelResponseDTO responsavel = null;
        ConfigurationsResponseDTO configurations = null;
        ContatoResponseDTO contato = null;
        Long id = null;
        String nome = null;
        String cnpj = null;
        String descricao = null;
        String logoUrl = null;

        colaboradores = colaboradorListToColaboradorResponseDTOList( entity.getColaboradores() );
        diretorias = diretoriaListToDiretoriaResponseDTOList( entity.getDiretorias() );
        noticias = noticiaListToNoticiaResponseDTOList( entity.getNoticias() );
        responsavel = responsavelMapper.toResponse( entity.getResponsavel() );
        configurations = configurationsMapper.toResponse( entity.getConfigurations() );
        contato = contatoMapper.toResponse( entity.getContato() );
        id = entity.getId();
        nome = entity.getNome();
        cnpj = entity.getCnpj();
        descricao = entity.getDescricao();
        logoUrl = entity.getLogoUrl();

        EmpresaJrResponseDTO empresaJrResponseDTO = new EmpresaJrResponseDTO( id, nome, cnpj, descricao, logoUrl, contato, configurations, responsavel, colaboradores, diretorias, noticias );

        return empresaJrResponseDTO;
    }

    @Override
    public EmpresaJr toEntity(EmpresaJrRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        EmpresaJr empresaJr = new EmpresaJr();

        empresaJr.setNome( dto.nome() );
        empresaJr.setCnpj( dto.cnpj() );
        empresaJr.setDescricao( dto.descricao() );
        empresaJr.setContato( contatoMapper.toEntity( dto.contato() ) );
        empresaJr.setConfigurations( configurationsMapper.toEntity( dto.configurations() ) );

        return empresaJr;
    }

    @Override
    public void updateEntity(EmpresaJrRequestDTO dto, EmpresaJr entity) {
        if ( dto == null ) {
            return;
        }

        if ( dto.nome() != null ) {
            entity.setNome( dto.nome() );
        }
        if ( dto.cnpj() != null ) {
            entity.setCnpj( dto.cnpj() );
        }
        if ( dto.descricao() != null ) {
            entity.setDescricao( dto.descricao() );
        }
        if ( dto.contato() != null ) {
            if ( entity.getContato() == null ) {
                entity.setContato( new Contato() );
            }
            contatoMapper.updateEntity( dto.contato(), entity.getContato() );
        }
        if ( dto.configurations() != null ) {
            if ( entity.getConfigurations() == null ) {
                entity.setConfigurations( new Configurations() );
            }
            configurationsMapper.updateEntity( dto.configurations(), entity.getConfigurations() );
        }
    }

    protected List<ColaboradorResponseDTO> colaboradorListToColaboradorResponseDTOList(List<Colaborador> list) {
        if ( list == null ) {
            return null;
        }

        List<ColaboradorResponseDTO> list1 = new ArrayList<ColaboradorResponseDTO>( list.size() );
        for ( Colaborador colaborador : list ) {
            list1.add( colaboradorMapper.toResponse( colaborador ) );
        }

        return list1;
    }

    protected List<DiretoriaResponseDTO> diretoriaListToDiretoriaResponseDTOList(List<Diretoria> list) {
        if ( list == null ) {
            return null;
        }

        List<DiretoriaResponseDTO> list1 = new ArrayList<DiretoriaResponseDTO>( list.size() );
        for ( Diretoria diretoria : list ) {
            list1.add( diretoriaMapper.toResponse( diretoria ) );
        }

        return list1;
    }

    protected List<NoticiaResponseDTO> noticiaListToNoticiaResponseDTOList(List<Noticia> list) {
        if ( list == null ) {
            return null;
        }

        List<NoticiaResponseDTO> list1 = new ArrayList<NoticiaResponseDTO>( list.size() );
        for ( Noticia noticia : list ) {
            list1.add( noticiaMapper.toResponse( noticia ) );
        }

        return list1;
    }
}
