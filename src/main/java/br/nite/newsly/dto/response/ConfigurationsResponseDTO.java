package br.nite.newsly.dto.response;

public class ConfigurationsResponseDTO {
    
    private String color;
    private byte[] logo;

    public ConfigurationsResponseDTO(String color, byte[] logo) {
        this.color = color;
        this.logo = logo;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public byte[] getLogo() {
        return logo;
    }

    public void setLogo(byte[] logo) {
        this.logo = logo;
    }
}
