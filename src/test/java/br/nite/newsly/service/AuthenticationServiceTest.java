package br.nite.newsly.service;

import br.nite.newsly.dto.response.LoginResponseDTO;
import br.nite.newsly.entity.Gestor;
import br.nite.newsly.entity.Responsavel;
import br.nite.newsly.entity.Usuario;
import br.nite.newsly.enums.Permissao;
import br.nite.newsly.repository.DiretorRepository;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class AuthenticationServiceTest {

    private AuthenticationService service;
    private JwtDecoder decoder;

    @BeforeEach
    void setUp() {
        SecretKey key = new SecretKeySpec(
                "test-secret-with-at-least-32-bytes-long".getBytes(StandardCharsets.UTF_8),
                "HmacSHA256");
        service = new AuthenticationService(
                new NimbusJwtEncoder(new ImmutableSecret<>(key)),
                mock(DiretorRepository.class));
        ReflectionTestUtils.setField(service, "expirationSeconds", 1800L);
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(key)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
        jwtDecoder.setJwtValidator(JwtValidators.createDefaultWithIssuer("newsly"));
        decoder = jwtDecoder;
    }

    @Test
    void emiteTokenBearerComPapeisEPermissoes() {
        Gestor gestor = new Gestor();
        gestor.setId(17L);
        gestor.setNome("Carlos");
        gestor.setPermissoes(List.of(Permissao.ESCRITOR));

        LoginResponseDTO response = service.criarResposta(gestor);
        Jwt decoded = decoder.decode(response.accessToken());

        assertEquals("Bearer", response.tokenType());
        assertEquals(1800, response.expiresIn());
        assertEquals("17", decoded.getSubject());
        assertTrue(decoded.getClaimAsStringList("authorities").contains("ROLE_GESTOR"));
        assertTrue(decoded.getClaimAsStringList("authorities").contains("PERM_ESCRITOR"));
        assertTrue(decoded.getClaimAsStringList("authorities").contains("PERM_LEITOR"));
    }

    @Test
    void administradorRecebeAcessoAdministrativo() {
        Usuario admin = new Usuario();
        admin.setId(2L);
        admin.setNome("Admin");
        admin.setAdministrador(true);

        LoginResponseDTO response = service.criarResposta(admin);
        Jwt decoded = decoder.decode(response.accessToken());

        assertEquals("ADMIN", response.usuario().tipo());
        assertTrue(decoded.getClaimAsStringList("authorities").contains("ROLE_ADMIN"));
        assertTrue(decoded.getClaimAsStringList("authorities").contains("PERM_ESCRITOR"));
    }

    @Test
    void responsavelRecebeSomenteRoleEspecificaParaAplicarEscopoDaPropriaEj() {
        Responsavel responsavel = new Responsavel();
        responsavel.setId(23L);
        responsavel.setNome("Maria");
        responsavel.setPermissoes(List.of(Permissao.LEITOR, Permissao.ESCRITOR));

        Jwt decoded = decoder.decode(service.criarResposta(responsavel).accessToken());
        List<String> authorities = decoded.getClaimAsStringList("authorities");

        assertTrue(authorities.contains("ROLE_RESPONSAVEL"));
        assertFalse(authorities.contains("PERM_LEITOR"));
        assertFalse(authorities.contains("PERM_ESCRITOR"));
    }
}
