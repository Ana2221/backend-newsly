package br.nite.newsly.entity;

import br.nite.newsly.enums.StatusSolicitacao;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Representa uma solicitação de publicação de notícia.
 * Guarda a submissão do Responsável enquanto aguarda decisão do Gestor.
 * A notícia só é criada após aprovação; o Responsável pode cancelar enquanto PENDENTE.
 */
@Entity
@Table(name = "solicitacao")
@Getter
@Setter
@NoArgsConstructor
public class Solicitacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "responsavel_id", nullable = false)
    private Responsavel responsavel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gestor_id")
    private Gestor gestor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "noticia_id")
    private Noticia noticia;

    @Column(name = "titulo_submetido", length = 255)
    private String tituloSubmetido;

    @Column(name = "conteudo_submetido", columnDefinition = "TEXT")
    private String conteudoSubmetido;

    @Column(name = "publicada_em_submetida")
    private LocalDateTime publicadaEmSubmetida;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id")
    private EmpresaJr empresa;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StatusSolicitacao status;

    @Column(name = "criada_em", nullable = false)
    private LocalDateTime criadaEm;

    @Column(name = "respondida_em")
    private LocalDateTime respondidaEm;

    @Column(name = "motivo_recusa", length = 500)
    private String motivoRecusa;
}
