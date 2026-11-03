package br.nite.newsly.controller;

import br.nite.newsly.dto.request.ResponsavelRequestDTO;
import br.nite.newsly.dto.response.ResponsavelResponseDTO;
import br.nite.newsly.security.SecurityAccess;
import br.nite.newsly.service.ResponsavelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/responsaveis")
@RequiredArgsConstructor
public class ResponsavelController {

    private final ResponsavelService service;
    private final SecurityAccess securityAccess;

    @PostMapping
    public ResponseEntity<ResponsavelResponseDTO> criar(
            @Valid @RequestBody ResponsavelRequestDTO dto,
            Authentication authentication) {
        boolean administrador = authentication != null && authentication.isAuthenticated()
                && authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        if (!administrador
                && (dto.empresaId() != null || (dto.permissoes() != null && !dto.permissoes().isEmpty()))) {
            throw new AccessDeniedException(
                    "O cadastro público de responsável não pode associar uma EJ ou definir permissões.");
        }

        ResponsavelRequestDTO request = administrador
                ? dto
                : new ResponsavelRequestDTO(dto.nome(), dto.senha(), dto.contato(), null, null);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityAccess.isSelf(authentication, #p0)")
    public ResponseEntity<ResponsavelResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PERM_LEITOR') or hasRole('RESPONSAVEL')")
    public ResponseEntity<List<ResponsavelResponseDTO>> buscarTodos() {
        if (securityAccess.isCurrentResponsible()) {
            return ResponseEntity.ok(List.of(service.buscarPorId(securityAccess.currentUserId())));
        }
        return ResponseEntity.ok(service.buscarTodos());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponsavelResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ResponsavelRequestDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
