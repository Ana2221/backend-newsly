package br.nite.newsly.entity;

import br.nite.newsly.enums.Permissao;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Subclasse de Usuario com estratégia JOINED.
 * Um Responsavel está associado a uma EmpresaJr (1:1).
 */
@Entity
@Table(name = "responsavel")
@PrimaryKeyJoinColumn(name = "usuario_id")
@DiscriminatorValue("RESPONSAVEL")
@Getter
@Setter
@NoArgsConstructor
public class Responsavel extends Usuario {

    @Column(name = "matricula", nullable = false, unique = true, columnDefinition = "BINARY(16)")
    private UUID matricula;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "responsavel_permissoes", joinColumns = @JoinColumn(name = "responsavel_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "permissao")
    private List<Permissao> permissoes = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", unique = true)
    private EmpresaJr empresa;
}
