package br.nite.newsly.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Subclasse de Usuario com estratégia JOINED.
 * A tabela colaborador possui FK para usuario(id).
 *
 * Um Colaborador pode ou não ser Diretor (0..1).
 * A relação com EmpresaJr é mapeada pelo lado da EmpresaJr.
 */
@Entity
@Table(name = "colaborador")
@PrimaryKeyJoinColumn(name = "usuario_id")
@DiscriminatorValue("COLABORADOR")
@Getter
@Setter
@NoArgsConstructor
public class Colaborador extends Usuario {

    @Column(name = "matricula", nullable = false, unique = true, columnDefinition = "BINARY(16)")
    private UUID matricula;

    @Column(name = "atividade", nullable = false)
    private Boolean atividade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id")
    private EmpresaJr empresa;
}
