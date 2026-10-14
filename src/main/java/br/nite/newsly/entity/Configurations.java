package br.nite.newsly.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Configurações visuais da EmpresaJr.
 * Embeddable — seus campos ficam na tabela empresa_jr.
 *
 * O campo logo foi removido: o armazenamento de imagens é feito
 * em disco via ArmazenamentoService, com o caminho salvo em logoUrl na entidade EmpresaJr.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class Configurations {

    @Column(name = "color", length = 20)
    private String color;
}
