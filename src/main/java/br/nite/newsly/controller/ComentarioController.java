package br.nite.newsly.controller;

import br.nite.newsly.dto.request.ComentarioRequestDTO;
import br.nite.newsly.dto.response.ComentarioResponseDTO;
import br.nite.newsly.service.ComentarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comentarios")
@RequiredArgsConstructor
public class ComentarioController {

    private final ComentarioService service;

    @PostMapping
    @PreAuthorize("hasAuthority('PERM_ESCRITOR') or hasRole('RESPONSAVEL') or hasRole('ADMIN')")
    public ResponseEntity<ComentarioResponseDTO> criar(@Valid @RequestBody ComentarioRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PERM_LEITOR') or hasRole('RESPONSAVEL') or hasRole('ADMIN')")
    public ResponseEntity<ComentarioResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PERM_LEITOR') or hasRole('RESPONSAVEL') or hasRole('ADMIN')")
    public ResponseEntity<List<ComentarioResponseDTO>> buscarTodos() {
        return ResponseEntity.ok(service.buscarTodos());
    }

    /**
     * Endpoint adicional: busca comentários raiz de uma notícia específica.
     * GET /comentarios/noticia/{noticiaId}
     */
    @GetMapping("/noticia/{noticiaId}")
    @PreAuthorize("hasAuthority('PERM_LEITOR') or hasRole('RESPONSAVEL') or hasRole('ADMIN')")
    public ResponseEntity<List<ComentarioResponseDTO>> buscarPorNoticia(@PathVariable Long noticiaId) {
        return ResponseEntity.ok(service.buscarPorNoticia(noticiaId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PERM_ESCRITOR') or hasRole('RESPONSAVEL') or hasRole('ADMIN')")
    public ResponseEntity<ComentarioResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ComentarioRequestDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PERM_ESCRITOR') or hasRole('ADMIN')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
