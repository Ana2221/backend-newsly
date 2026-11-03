package br.nite.newsly.controller;

import br.nite.newsly.configuration.SecurityConfiguration;
import br.nite.newsly.dto.response.EmpresaJrResponseDTO;
import br.nite.newsly.dto.response.EmpresaJrPublicaResponseDTO;
import br.nite.newsly.security.SecurityAccess;
import br.nite.newsly.service.EmpresaJrService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmpresaJrController.class)
@Import(SecurityConfiguration.class)
@ActiveProfiles("test")
@TestPropertySource(properties = "security.jwt.secret=test-secret-with-at-least-32-bytes-long")
class EmpresaResponsavelAuthorizationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtEncoder jwtEncoder;

    @MockBean
    private EmpresaJrService empresaService;

    @MockBean(name = "securityAccess")
    private SecurityAccess securityAccess;

    @Test
    void usuarioAnonimoPodeConsultarEmpresaCadastradaPorId() throws Exception {
        when(empresaService.buscarResumoPublicoPorId(99L)).thenReturn(
                new EmpresaJrPublicaResponseDTO(99L, "Empresa pública", "11.222.333/0001-81",
                        "Descrição pública", null));

        mockMvc.perform(get("/empresas/99"))
                .andExpect(status().isOk());

        verify(empresaService).buscarResumoPublicoPorId(99L);
        verify(empresaService, never()).buscarPorId(99L);
    }

    @Test
    void usuarioAnonimoPodeConsultarListaDeEmpresas() throws Exception {
        when(empresaService.buscarResumosPublicos()).thenReturn(List.of(
                new EmpresaJrPublicaResponseDTO(99L, "Empresa pública", "11.222.333/0001-81",
                        "Descrição pública", null)));

        mockMvc.perform(get("/empresas"))
                .andExpect(status().isOk());

        verify(empresaService).buscarResumosPublicos();
    }

    @Test
    void respostaPublicaNaoExpõeContatoOuMembros() throws Exception {
        when(empresaService.buscarResumoPublicoPorId(99L)).thenReturn(
                new EmpresaJrPublicaResponseDTO(99L, "Empresa pública", "11.222.333/0001-81",
                        "Descrição pública", null));

        mockMvc.perform(get("/empresas/99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Empresa pública"))
                .andExpect(jsonPath("$.contato").doesNotExist())
                .andExpect(jsonPath("$.colaboradores").doesNotExist())
                .andExpect(jsonPath("$.responsavel").doesNotExist());
    }

    @Test
    void colaboradorPodeConsultarResumoPublicoDeEmpresas() throws Exception {
        when(empresaService.buscarResumosPublicos()).thenReturn(List.of());

        mockMvc.perform(get("/empresas")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token("ROLE_COLABORADOR")))
                .andExpect(status().isOk());

        verify(empresaService).buscarResumosPublicos();
    }

    @Test
    void colaboradorNaoPodeCadastrarEmpresa() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/empresas")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token("ROLE_COLABORADOR"))
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Empresa teste",
                                  "cnpj": "12.345.678/0001-90"
                                }
                                """))
                .andExpect(status().isForbidden());

        verifyNoInteractions(empresaService);
    }

    @Test
    void responsavelPodeConsultarPropriaEmpresa() throws Exception {
        when(securityAccess.isCurrentResponsible()).thenReturn(true);
        when(securityAccess.isOwnEmpresa(42L)).thenReturn(true);
        when(empresaService.buscarPorId(42L)).thenReturn(
                new EmpresaJrResponseDTO(42L, "Minha EJ", "11.222.333/0001-81",
                        null, null, null, null, null, List.of(), List.of(), List.of()));

        mockMvc.perform(get("/empresas/42")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token("ROLE_RESPONSAVEL")))
                .andExpect(status().isOk());

        verify(empresaService).buscarPorId(42L);
    }

    @Test
    void responsavelNaoPodeConsultarEmpresaDeOutraEj() throws Exception {
        when(securityAccess.isCurrentResponsible()).thenReturn(true);
        when(securityAccess.isOwnEmpresa(99L)).thenReturn(false);

        mockMvc.perform(get("/empresas/99")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token("ROLE_RESPONSAVEL")))
                .andExpect(status().isForbidden());

        verify(empresaService, never()).buscarPorId(99L);
        verify(empresaService, never()).buscarResumoPublicoPorId(99L);
    }

    @Test
    void responsavelNaoPodeExcluirNemMesmoAPropriaEmpresa() throws Exception {
        mockMvc.perform(delete("/empresas/42")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token("ROLE_RESPONSAVEL")))
                .andExpect(status().isForbidden());

        verifyNoInteractions(empresaService);
    }

    @Test
    void responsavelPodeCriarEmpresa() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/empresas")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token("ROLE_RESPONSAVEL"))
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Minha EJ",
                                  "cnpj": "CNPJ-DE-TESTE",
                                  "contato": {"email": "contato@minhaej.com"},
                                  "responsavelId": 999
                                }
                                """))
                .andExpect(status().isCreated());

        verify(empresaService).criar(argThat(dto ->
                dto.responsavelId().equals(999L)
                        && dto.cnpj().equals("CNPJ-DE-TESTE")
                        && dto.contato().email().equals("contato@minhaej.com")));
    }

    private String token(String... authorities) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("newsly")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(1800))
                .subject("7")
                .claim("authorities", List.of(authorities))
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(), claims)).getTokenValue();
    }
}
