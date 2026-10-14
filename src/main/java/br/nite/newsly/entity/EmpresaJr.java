package br.nite.newsly.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "empresa_jr")
@Getter
@Setter
@NoArgsConstructor
public class EmpresaJr {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @Column(name = "cnpj", nullable = false, unique = true, length = 18)
    private String cnpj;

    @Column(name = "descricao", length = 500)
    private String descricao;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Embedded
    private Contato contato;

    @Embedded
    private Configurations configurations;

    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Diretoria> diretorias = new ArrayList<>();

    @OneToMany(mappedBy = "empresa", cascade = CascadeType.PERSIST)
    private List<Colaborador> colaboradores = new ArrayList<>();

    @OneToOne(mappedBy = "empresa", cascade = CascadeType.ALL, orphanRemoval = true)
    private Responsavel responsavel;

    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Noticia> noticias = new ArrayList<>();
}
