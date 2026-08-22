package br.nite.newsly.mapper;

import br.nite.newsly.dto.request.ConfigurationsRequestDTO;
import br.nite.newsly.dto.request.EmpresaJrRequestDTO;
import br.nite.newsly.dto.response.ColaboradorResponseDTO;
import br.nite.newsly.dto.response.ConfigurationsResponseDTO;
import br.nite.newsly.dto.response.ContatoResponseDTO;
import br.nite.newsly.dto.response.DiretoriaResponseDTO;
import br.nite.newsly.dto.response.EmpresaJrResponseDTO;
import br.nite.newsly.dto.response.NoticiaResponseDTO;
import br.nite.newsly.entity.Colaborador;
import br.nite.newsly.entity.Configurations;
import br.nite.newsly.entity.Contato;
import br.nite.newsly.entity.Diretoria;
import br.nite.newsly.entity.EmpresaJr;
import br.nite.newsly.entity.Noticia;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-19T17:39:38-0300",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.100.v20260624-0231, environment: Java 21.0.11 (Eclipse Adoptium)"
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
    private ContatoMapper contatoMapper;
    @Autowired
    private ConfigurationsMapper configurationsMapper;

    @Override
    public EmpresaJrResponseDTO toResponse(EmpresaJr entity) {
        if ( entity == null ) {
            return null;
        }

        List<ColaboradorResponseDTO> colaboradores = null;
        List<DiretoriaResponseDTO> diretorias = null;
        List<NoticiaResponseDTO> noticias = null;
        Long id = null;
        String nome = null;
        String cnpj = null;
        ContatoResponseDTO contato = null;
        ConfigurationsResponseDTO configurations = null;
        String descricao = null;

        colaboradores = colaboradorListToColaboradorResponseDTOList( entity.getColaboradores() );
        diretorias = diretoriaListToDiretoriaResponseDTOList( entity.getDiretorias() );
        noticias = noticiaListToNoticiaResponseDTOList( entity.getNoticias() );
        id = entity.getId();
        nome = entity.getNome();
        cnpj = entity.getCnpj();
        contato = contatoMapper.toResponse( entity.getContato() );
        configurations = configurationsMapper.toResponse( entity.getConfigurations() );
        descricao = entity.getDescricao();

        EmpresaJrResponseDTO empresaJrResponseDTO = new EmpresaJrResponseDTO( id, nome, cnpj, colaboradores, diretorias, noticias, contato, configurations, descricao );

        return empresaJrResponseDTO;
    }

    @Override
    public EmpresaJr toEntity(EmpresaJrRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        EmpresaJr empresaJr = new EmpresaJr();

        empresaJr.setCnpj( dto.cnpj() );
        empresaJr.setConfigurations( configurationsRequestDTOToConfigurations( dto.configurations() ) );
        empresaJr.setContato( contatoMapper.toEntity( dto.contato() ) );
        empresaJr.setDescricao( dto.descricao() );
        empresaJr.setNome( dto.nome() );

        return empresaJr;
    }

    @Override
    public void updateEntity(EmpresaJrRequestDTO dto, EmpresaJr entity) {
        if ( dto == null ) {
            return;
        }

        entity.setCnpj( dto.cnpj() );
        if ( dto.configurations() != null ) {
            if ( entity.getConfigurations() == null ) {
                entity.setConfigurations( new Configurations() );
            }
            configurationsRequestDTOToConfigurations1( dto.configurations(), entity.getConfigurations() );
        }
        else {
            entity.setConfigurations( null );
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
        entity.setDescricao( dto.descricao() );
        entity.setNome( dto.nome() );
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

    protected Configurations configurationsRequestDTOToConfigurations(ConfigurationsRequestDTO configurationsRequestDTO) {
        if ( configurationsRequestDTO == null ) {
            return null;
        }

        Configurations configurations = new Configurations();

        configurations.setColor( configurationsRequestDTO.color() );
        byte[] logo = configurationsRequestDTO.logo();
        if ( logo != null ) {
            configurations.setLogo( Arrays.copyOf( logo, logo.length ) );
        }

        return configurations;
    }

    protected void configurationsRequestDTOToConfigurations1(ConfigurationsRequestDTO configurationsRequestDTO, Configurations mappingTarget) {
        if ( configurationsRequestDTO == null ) {
            return;
        }

        mappingTarget.setColor( configurationsRequestDTO.color() );
        byte[] logo = configurationsRequestDTO.logo();
        if ( logo != null ) {
            mappingTarget.setLogo( Arrays.copyOf( logo, logo.length ) );
        }
        else {
            mappingTarget.setLogo( null );
        }
    }
}
