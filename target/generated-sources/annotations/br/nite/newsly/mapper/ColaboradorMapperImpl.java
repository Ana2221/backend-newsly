package br.nite.newsly.mapper;

import br.nite.newsly.dto.request.ColaboradorRequestDTO;
import br.nite.newsly.dto.response.ColaboradorResponseDTO;
import br.nite.newsly.dto.response.ContatoResponseDTO;
import br.nite.newsly.entity.Colaborador;
import br.nite.newsly.entity.Contato;
import br.nite.newsly.entity.EmpresaJr;
import br.nite.newsly.enums.TipoUsuario;
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
public class ColaboradorMapperImpl implements ColaboradorMapper {

    @Autowired
    private ContatoMapper contatoMapper;

    @Override
    public ColaboradorResponseDTO toResponse(Colaborador entity) {
        if ( entity == null ) {
            return null;
        }

        Long empresaId = null;
        Long id = null;
        UUID matricula = null;
        String nome = null;
        Boolean atividade = null;
        ContatoResponseDTO contato = null;

        empresaId = entityEmpresaId( entity );
        id = entity.getId();
        matricula = entity.getMatricula();
        nome = entity.getNome();
        atividade = entity.getAtividade();
        contato = contatoMapper.toResponse( entity.getContato() );

        TipoUsuario tipo = TipoUsuario.COLABORADOR;

        ColaboradorResponseDTO colaboradorResponseDTO = new ColaboradorResponseDTO( id, matricula, nome, atividade, contato, empresaId, tipo );

        return colaboradorResponseDTO;
    }

    @Override
    public Colaborador toEntity(ColaboradorRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Colaborador colaborador = new Colaborador();

        colaborador.setContato( contatoMapper.toEntity( dto.contato() ) );
        colaborador.setNome( dto.nome() );
        colaborador.setSenha( dto.senha() );
        colaborador.setAtividade( dto.atividade() );

        colaborador.setMatricula( UUID.randomUUID() );

        return colaborador;
    }

    @Override
    public void updateEntity(ColaboradorRequestDTO dto, Colaborador entity) {
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
        entity.setAtividade( dto.atividade() );
    }

    private Long entityEmpresaId(Colaborador colaborador) {
        EmpresaJr empresa = colaborador.getEmpresa();
        if ( empresa == null ) {
            return null;
        }
        return empresa.getId();
    }
}
