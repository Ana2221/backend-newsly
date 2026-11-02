package br.nite.newsly.service;

import br.nite.newsly.exception.ExternalValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class CnpjValidationServiceTest {

    private static final String CNPJ = "11.222.333/0001-81";
    private static final String CNPJ_DIGITS = "11222333000181";

    @Test
    void validaCnpjConsultandoBrasilApi() {
        RestTemplate restTemplate = new RestTemplate();
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
        server.expect(requestTo("https://brasilapi.test/" + CNPJ_DIGITS))
                .andRespond(withSuccess("{\"cnpj\":\"" + CNPJ_DIGITS + "\"}", MediaType.APPLICATION_JSON));
        CnpjValidationService service = new CnpjValidationService(
                restTemplate, "https://brasilapi.test/", true);

        service.validar(CNPJ);

        server.verify();
    }

    @Test
    void falhaQuandoCnpjNaoExisteNaBrasilApi() {
        RestTemplate restTemplate = new RestTemplate();
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
        server.expect(requestTo("https://brasilapi.test/" + CNPJ_DIGITS))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));
        CnpjValidationService service = new CnpjValidationService(
                restTemplate, "https://brasilapi.test", true);

        assertThrows(IllegalArgumentException.class, () -> service.validar(CNPJ));
        server.verify();
    }

    @Test
    void naoValidaCnpjNemConsultaBrasilApiQuandoConsultaExternaEstaDesabilitada() {
        RestTemplate restTemplate = new RestTemplate();
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
        CnpjValidationService service = new CnpjValidationService(
                restTemplate, "https://brasilapi.test", false);

        service.validar("11.222.333/0001-80");
        server.verify();
    }

    @Test
    void informaIndisponibilidadeDaBrasilApi() {
        RestTemplate restTemplate = new RestTemplate();
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
        server.expect(requestTo("https://brasilapi.test/" + CNPJ_DIGITS))
                .andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE));
        CnpjValidationService service = new CnpjValidationService(
                restTemplate, "https://brasilapi.test", true);

        assertThrows(ExternalValidationException.class, () -> service.validar(CNPJ));
        server.verify();
    }
}
