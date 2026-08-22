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
 * A tabela gestor possui FK para usuario(id).
 *
 * As permissões são armazenadas em tabela auxiliar via @ElementCollection.
 */
@Entity
@Table(name = "gestor")
@PrimaryKeyJoinColumn(name = "usuario_id")
@DiscriminatorValue("GESTOR")
@Getter
@Setter
@NoArgsConstructor
public class Gestor extends Usuario {

    @Column(name = "matricula", nullable = false, unique = true, columnDefinition = "BINARY(16)")
    private UUID matricula;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "gestor_permissoes", joinColumns = @JoinColumn(name = "gestor_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "permissao")
    private List<Permissao> permissoes = new ArrayList<>();
}
