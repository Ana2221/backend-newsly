package br.nite.newsly.entity;

import br.nite.newsly.enums.Permissao;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Diretor não possui ID próprio no UML.
 * Decisão técnica: usa @MapsId compartilhando o ID do Colaborador associado.
 *
 * Cardinalidade atualizada: um Colaborador pode ser Diretor em múltiplas Diretorias (N:1).
 * A constraint unique foi removida do lado diretoria_id.
 */
@Entity
@Table(name = "diretor")
@Getter
@Setter
@NoArgsConstructor
public class Diretor {

    @Id
    @Column(name = "colaborador_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "colaborador_id")
    private Colaborador colaborador;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "funcao_id", nullable = false)
    private Funcao funcao;

    // Cardinalidade N:1 — múltiplos diretores podem pertencer à mesma Diretoria
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "diretoria_id", nullable = false)
    private Diretoria diretoria;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "diretor_permissoes", joinColumns = @JoinColumn(name = "diretor_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "permissao")
    private List<Permissao> permissoes = new ArrayList<>();
}
