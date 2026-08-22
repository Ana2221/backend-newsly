package br.nite.newsly.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Comentario possui relacionamento recursivo:
 *   Comentario "1" -- "0..*" Comentario : respostas
 *
 * Um comentário pode ser resposta de outro (pai opcional).
 * Um comentário pode ter 0..* respostas (filhos).
 */
@Entity
@Table(name = "comentario")
@Getter
@Setter
@NoArgsConstructor
public class Comentario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "conteudo", nullable = false, columnDefinition = "TEXT")
    private String conteudo;

    @Column(name = "publicado_em", nullable = false)
    private LocalDateTime publicadoEm;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "noticia_id", nullable = false)
    private Noticia noticia;

    // Relacionamento recursivo: comentário pai (opcional)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comentario_pai_id")
    private Comentario comentarioPai;

    // Relacionamento recursivo: respostas a este comentário
    @OneToMany(mappedBy = "comentarioPai", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comentario> respostas = new ArrayList<>();
}
