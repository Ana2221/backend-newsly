package br.nite.newsly.service;

import br.nite.newsly.dto.response.LoginResponseDTO;
import br.nite.newsly.entity.Colaborador;
import br.nite.newsly.entity.Gestor;
import br.nite.newsly.entity.Responsavel;
import br.nite.newsly.entity.Usuario;
import br.nite.newsly.enums.Permissao;
import br.nite.newsly.repository.DiretorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final JwtEncoder jwtEncoder;
    private final DiretorRepository diretorRepository;

    @Value("${security.jwt.expiration-seconds:1800}")
    private long expirationSeconds = 1800;

    public LoginResponseDTO criarResposta(Usuario usuario) {
        if (expirationSeconds <= 0) {
            throw new IllegalStateException("security.jwt.expiration-seconds deve ser maior que zero.");
        }

        List<String> permissions = obterPermissoes(usuario);
        if (usuario.isAdministrador()) {
            permissions = List.of(Permissao.LEITOR.name(), Permissao.ESCRITOR.name());
        }
        String tipo = obterTipo(usuario);
        List<String> authorities = new ArrayList<>();
        authorities.add("ROLE_" + tipo);
        if (!(usuario instanceof Responsavel)) {
            permissions.forEach(permission -> authorities.add("PERM_" + permission));
            if (permissions.contains(Permissao.ESCRITOR.name())) {
                authorities.add("PERM_LEITOR");
            }
        }
        if (usuario.isAdministrador()) {
            authorities.add("ROLE_ADMIN");
            authorities.add("PERM_LEITOR");
            authorities.add("PERM_ESCRITOR");
        }

        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plusSeconds(expirationSeconds);
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("newsly")
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .subject(usuario.getId().toString())
                .claim("authorities", authorities.stream().distinct().toList())
                .build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(), claims)).getTokenValue();

        String email = usuario.getContato() == null ? null : usuario.getContato().getEmail();
        return new LoginResponseDTO(
                token,
                "Bearer",
                expirationSeconds,
                new LoginResponseDTO.AuthenticatedUserDTO(
                        usuario.getId(),
                        usuario.getNome(),
                        email,
                        usuario.isAdministrador() ? "ADMIN" : tipo,
                        permissions));
    }

    private List<String> obterPermissoes(Usuario usuario) {
        if (usuario instanceof Gestor gestor) {
            return gestor.getPermissoes().stream().map(Enum::name).distinct().toList();
        }
        if (usuario instanceof Responsavel responsavel) {
            return responsavel.getPermissoes().stream().map(Enum::name).distinct().toList();
        }
        if (usuario instanceof Colaborador colaborador) {
            return diretorRepository.findByColaboradorId(colaborador.getId())
                    .map(diretor -> diretor.getPermissoes().stream().map(Enum::name).distinct().toList())
                    .orElseGet(List::of);
        }
        return List.of();
    }

    private String obterTipo(Usuario usuario) {
        if (usuario instanceof Gestor) {
            return "GESTOR";
        }
        if (usuario instanceof Responsavel) {
            return "RESPONSAVEL";
        }
        if (usuario instanceof Colaborador) {
            return "COLABORADOR";
        }
        return "USUARIO";
    }
}
