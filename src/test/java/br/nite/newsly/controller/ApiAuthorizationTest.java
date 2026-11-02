package br.nite.newsly.controller;

import br.nite.newsly.configuration.SecurityConfiguration;
import br.nite.newsly.service.NoticiaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NoticiaController.class)
@Import(SecurityConfiguration.class)
@ActiveProfiles("test")
@TestPropertySource(properties = "security.jwt.secret=test-secret-with-at-least-32-bytes-long")
class ApiAuthorizationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtEncoder jwtEncoder;

    @MockBean
    private NoticiaService noticiaService;

    @Test
    void bloqueiaConsultaSemToken() throws Exception {
        mockMvc.perform(get("/noticias"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void leitorPodeConsultarNoticias() throws Exception {
        when(noticiaService.buscarTodos()).thenReturn(List.of());

        mockMvc.perform(get("/noticias")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token("PERM_LEITOR")))
                .andExpect(status().isOk());
    }

    @Test
    void leitorNaoPodeCriarNoticia() throws Exception {
        mockMvc.perform(post("/noticias")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token("PERM_LEITOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "titulo": "Teste",
                                  "conteudo": "Conteúdo de teste",
                                  "publicadaEm": "2026-10-30T12:00:00",
                                  "empresaId": 1
                                }
                                """))
                .andExpect(status().isForbidden());

        verifyNoInteractions(noticiaService);
    }

    @Test
    void responsavelDeveSubmeterNoticiaParaAprovacao() throws Exception {
        mockMvc.perform(post("/noticias")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token("ROLE_RESPONSAVEL"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "titulo": "Submissão",
                                  "conteudo": "Conteúdo para aprovação",
                                  "publicadaEm": "2026-10-30T12:00:00",
                                  "empresaId": 1
                                }
                                """))
                .andExpect(status().isForbidden());

        verifyNoInteractions(noticiaService);
    }

    private String token(String... authorities) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("newsly")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(1800))
                .subject("1")
                .claim("authorities", List.of(authorities))
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(), claims)).getTokenValue();
    }
}
