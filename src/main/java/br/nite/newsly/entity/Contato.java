package br.nite.newsly.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Contato é modelado como @Embeddable pois não possui
 * ciclo de vida próprio nem ID definido no UML.
 * Seus campos serão colunas na tabela usuario.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class Contato {

    @Column(name = "telefone", length = 20,unique = true)
    private String telefone;

    @Column(name = "email", length = 150,unique = true)
    private String email;
}
