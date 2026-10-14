package br.nite.newsly.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "noticia")
@Getter
@Setter
@NoArgsConstructor
public class Noticia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "titulo", nullable = false, length = 255)
    private String titulo;

    @Column(name = "conteudo", nullable = false, columnDefinition = "TEXT")
    private String conteudo;

    @Column(name = "publicada_em", nullable = false)
    private LocalDateTime publicadaEm;

    @Column(name = "imagem_url", length = 500)
    private String imagemUrl;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empresa_id", nullable = false)
    private EmpresaJr empresa;

    @OneToMany(mappedBy = "noticia", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comentario> comentarios = new ArrayList<>();
}
