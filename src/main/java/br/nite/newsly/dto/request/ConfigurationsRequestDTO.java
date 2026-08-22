package br.nite.newsly.dto.request;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Lob;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


public record ConfigurationsRequestDTO (

    @Column(name = "color", length = 20,unique = true)
    String color,

    @Lob
    @Column(columnDefinition = "LONGBLOB") 
    byte[] logo
){}
