package br.nite.newsly.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidade base da hierarquia de herança.
 * Estratégia JOINED: cada subclasse possui sua própria tabela
 * com FK apontando para a tabela usuario.
 *
 * Contato é @Embedded: telefone e email ficam na tabela usuario.
 */
@Entity
@Table(name = "usuario")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "tipo", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@NoArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @Column(name = "senha", nullable = false, length = 255)
    private String senha;

    @Embedded
    private Contato contato;
}
