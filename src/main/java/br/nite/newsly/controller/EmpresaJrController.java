package br.nite.newsly.controller;

import br.nite.newsly.dto.request.EmpresaJrRequestDTO;
import br.nite.newsly.dto.response.EmpresaJrResponseDTO;
import br.nite.newsly.security.SecurityAccess;
import br.nite.newsly.service.EmpresaJrService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/empresas")
@RequiredArgsConstructor
public class EmpresaJrController {

    private final EmpresaJrService service;
    private final SecurityAccess securityAccess;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('RESPONSAVEL')")
    public ResponseEntity<EmpresaJrResponseDTO> criar(@Valid @RequestBody EmpresaJrRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @PatchMapping("/{id}/logo")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('RESPONSAVEL') and @securityAccess.isOwnEmpresa(#p0))")
    public ResponseEntity<EmpresaJrResponseDTO> uploadLogo(
            @PathVariable Long id,
            @RequestParam("arquivo") MultipartFile arquivo) {
        return ResponseEntity.ok(service.uploadLogo(id, arquivo));
    }

    @PatchMapping("/adicionar-membro/{idmembro}/{idempresa}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('RESPONSAVEL') and @securityAccess.isOwnEmpresa(#p1))")
    public void addmembro(@PathVariable Long idmembro, @PathVariable Long idempresa) {
        service.adicionarMembro(idmembro, idempresa);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id, Authentication authentication) {
        if (securityAccess.isCurrentResponsible() && !securityAccess.isOwnEmpresa(id)) {
            throw new AccessDeniedException("Acesso não permitido.");
        }
        if (isPublicViewer(authentication)) {
            return ResponseEntity.ok(service.buscarResumoPublicoPorId(id));
        }
        requireAuthenticatedReader(authentication);
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping
    public ResponseEntity<?> buscarTodos(Authentication authentication) {
        if (isPublicViewer(authentication)) {
            return ResponseEntity.ok(service.buscarResumosPublicos());
        }
        requireAuthenticatedReader(authentication);
        if (securityAccess.isCurrentResponsible()) {
            return ResponseEntity.ok(List.of(service.buscarPorId(securityAccess.currentCompanyId())));
        }
        return ResponseEntity.ok(service.buscarTodos());
    }

    private boolean isPublicViewer(Authentication authentication) {
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return true;
        }
        boolean administrador = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        boolean colaborador = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_COLABORADOR"));
        return colaborador && !administrador;
    }

    private void requireAuthenticatedReader(Authentication authentication) {
        boolean canRead = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN")
                        || authority.getAuthority().equals("ROLE_RESPONSAVEL")
                        || authority.getAuthority().equals("PERM_LEITOR"));
        if (!canRead) {
            throw new AccessDeniedException("Acesso não permitido.");
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('RESPONSAVEL') and @securityAccess.isOwnEmpresa(#p0))")
    public ResponseEntity<EmpresaJrResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody EmpresaJrRequestDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
