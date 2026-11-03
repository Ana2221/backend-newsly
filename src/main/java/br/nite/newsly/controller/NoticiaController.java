package br.nite.newsly.controller;

import br.nite.newsly.dto.request.NoticiaRequestDTO;
import br.nite.newsly.dto.response.NoticiaResponseDTO;
import br.nite.newsly.service.NoticiaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/noticias")
@RequiredArgsConstructor
public class NoticiaController {

    private final NoticiaService service;

    @PostMapping
    @PreAuthorize("hasAuthority('PERM_ESCRITOR') or hasRole('ADMIN')")
    public ResponseEntity<NoticiaResponseDTO> criar(@Valid @RequestBody NoticiaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @PatchMapping("/{id}/imagem")
    @PreAuthorize("hasAuthority('PERM_ESCRITOR') or hasRole('RESPONSAVEL') or hasRole('ADMIN')")
    public ResponseEntity<NoticiaResponseDTO> uploadImagem(
            @PathVariable Long id,
            @RequestParam("arquivo") MultipartFile arquivo) {
        return ResponseEntity.ok(service.uploadImagem(id, arquivo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoticiaResponseDTO> buscarPorId(
            @PathVariable Long id, Authentication authentication) {
        requirePublicOrReader(authentication);
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<NoticiaResponseDTO>> buscarTodos(Authentication authentication) {
        requirePublicOrReader(authentication);
        return ResponseEntity.ok(service.buscarTodos());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PERM_ESCRITOR') or hasRole('RESPONSAVEL') or hasRole('ADMIN')")
    public ResponseEntity<NoticiaResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody NoticiaRequestDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PERM_ESCRITOR') or hasRole('ADMIN')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }

    private void requirePublicOrReader(Authentication authentication) {
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return;
        }
        boolean canRead = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_COLABORADOR")
                        || authority.getAuthority().equals("ROLE_RESPONSAVEL")
                        || authority.getAuthority().equals("ROLE_ADMIN")
                        || authority.getAuthority().equals("PERM_LEITOR"));
        if (!canRead) {
            throw new AccessDeniedException("Acesso não permitido.");
        }
    }
}
