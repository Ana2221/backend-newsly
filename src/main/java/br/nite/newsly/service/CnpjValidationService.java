package br.nite.newsly.service;

import br.nite.newsly.exception.ExternalValidationException;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Service
public class CnpjValidationService {

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final boolean enabled;

    @Autowired
    public CnpjValidationService(
            RestTemplateBuilder builder,
            @Value("${newsly.cnpj-validation.base-url:https://brasilapi.com.br/api/cnpj/v1}") String baseUrl,
            @Value("${newsly.cnpj-validation.enabled:false}") boolean enabled) {
        this(builder
                .setConnectTimeout(Duration.ofSeconds(3))
                .setReadTimeout(Duration.ofSeconds(5))
                .build(), baseUrl, enabled);
    }

    CnpjValidationService(RestTemplate restTemplate, String baseUrl, boolean enabled) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl.replaceAll("/+$", "");
        this.enabled = enabled;
    }

    public void validar(String cnpj) {
        String digits = cnpj.replaceAll("\\D", "");
        if (!enabled) {
            return;
        }

        try {
            JsonNode response = restTemplate.getForObject(baseUrl + "/" + digits, JsonNode.class);
            if (response == null || !digits.equals(response.path("cnpj").asText())) {
                throw new IllegalArgumentException("O CNPJ informado não foi validado pela BrasilAPI.");
            }
        } catch (HttpStatusCodeException ex) {
            if (ex.getStatusCode() == HttpStatus.BAD_REQUEST || ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new IllegalArgumentException("O CNPJ informado não foi encontrado ou é inválido.", ex);
            }
            throw new ExternalValidationException("Não foi possível validar o CNPJ na BrasilAPI.", ex);
        } catch (RestClientException ex) {
            throw new ExternalValidationException("Não foi possível validar o CNPJ na BrasilAPI.", ex);
        }
    }
}
