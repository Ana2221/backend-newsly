package br.nite.newsly.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

/**
 * Expõe a pasta local "uploads" como recurso estático acessível via HTTP.
 *
 * Qualquer arquivo salvo em {user.dir}/uploads/ poderá ser acessado em:
 *   GET https://backend-newsly.onrender.com/uploads/{subpasta}/{arquivo}
 *
 * Exemplos:
 *   GET /uploads/noticias/uuid.jpg
 *   GET /uploads/logos/uuid.png
 *
 * Para migrar para S3 no futuro: remova ou desative esta configuração
 * e retorne a URL completa do S3 no ArmazenamentoService.
 */
@Configuration
public class StaticResourceConfiguration implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String pastaUploads = Paths.get(System.getProperty("user.dir"), "uploads")
                .toAbsolutePath()
                .toUri()
                .toString();

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(pastaUploads);
    }
}
