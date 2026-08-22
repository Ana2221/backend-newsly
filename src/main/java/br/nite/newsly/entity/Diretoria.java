package br.nite.newsly.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Diretoria pertence obrigatoriamente a uma EmpresaJr (1..*).
 * Uma Diretoria pode ter múltiplos Diretores (1:N).
 */
@Entity
@Table(name = "diretoria")
@Getter
@Setter
@NoArgsConstructor
public class Diretoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "criada_em", nullable = false)
    private LocalDate criadaEm;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empresa_id", nullable = false)
    private EmpresaJr empresa;

    // Relacionamento inverso: lista de Diretores desta Diretoria
    @OneToMany(mappedBy = "diretoria", fetch = FetchType.LAZY)
    private List<Diretor> diretores = new ArrayList<>();
}
