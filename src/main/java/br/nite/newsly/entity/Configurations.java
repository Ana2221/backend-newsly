package br.nite.newsly.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Lob;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class Configurations {

    @Column(name = "color", length = 20,unique = true)
    private String color;

    @Lob
    @Column(columnDefinition = "LONGBLOB") // ou BLOB dependendo do banco
    private byte[] logo;
}
