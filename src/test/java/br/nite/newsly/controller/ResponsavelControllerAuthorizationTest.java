package br.nite.newsly.controller;

import br.nite.newsly.configuration.SecurityConfiguration;
import br.nite.newsly.dto.response.ResponsavelResponseDTO;
import br.nite.newsly.enums.TipoUsuario;
import br.nite.newsly.security.SecurityAccess;
import br.nite.newsly.service.ResponsavelService;
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

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ResponsavelController.class)
@Import(SecurityConfiguration.class)
@ActiveProfiles("test")
@TestPropertySource(properties = "security.jwt.secret=test-secret-with-at-least-32-bytes-long")
class ResponsavelControllerAuthorizationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtEncoder jwtEncoder;

    @MockBean
    private ResponsavelService responsavelService;

    @MockBean(name = "securityAccess")
    private SecurityAccess securityAccess;

    @Test
    void usuarioAnonimoPodeCriarResponsavelSemEmpresaOuPermissoes() throws Exception {
        when(responsavelService.criar(any())).thenReturn(
                new ResponsavelResponseDTO(1L, null, "Maria", null, List.of(), null, TipoUsuario.RESPONSAVEL));

        mockMvc.perform(post("/responsaveis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Maria Costa",
                                  "senha": "senha123",
                                  "contato": {"email": "maria@example.com"}
                                }
                                """))
                .andExpect(status().isCreated());

        verify(responsavelService).criar(argThat(dto ->
                dto.empresaId() == null && dto.permissoes() == null
                        && dto.nome().equals("Maria Costa")));
    }

    @Test
    void usuarioAnonimoNaoPodeAssociarEmpresaAoCriarResponsavel() throws Exception {
        mockMvc.perform(post("/responsaveis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Maria Costa",
                                  "senha": "senha123",
                                  "empresaId": 1
                                }
                                """))
                .andExpect(status().isForbidden());

        verifyNoInteractions(responsavelService);
    }

    @Test
    void administradorPodeCriarResponsavelAssociadoAEmpresa() throws Exception {
        when(responsavelService.criar(any())).thenReturn(
                new ResponsavelResponseDTO(1L, null, "Maria", null, List.of(), 42L, TipoUsuario.RESPONSAVEL));

        mockMvc.perform(post("/responsaveis")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token("ROLE_ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Maria Costa",
                                  "senha": "senha123",
                                  "empresaId": 42
                                }
                                """))
                .andExpect(status().isCreated());

        verify(responsavelService).criar(argThat(dto -> dto.empresaId().equals(42L)));
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
