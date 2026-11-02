package br.nite.newsly.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Representa a função exercida por um Diretor.
 * ID gerado automaticamente pois o UML não definiu — decisão técnica necessária
 * para permitir persistência JPA.
 */
@Entity
@Table(name = "funcao")
@Getter
@Setter
@NoArgsConstructor
public class Funcao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "descricao", length = 500)
    private String descricao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id")
    private EmpresaJr empresa;
}
