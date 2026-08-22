package br.nite.newsly.controller;

import br.nite.newsly.dto.request.ComentarioRequestDTO;
import br.nite.newsly.dto.response.ComentarioResponseDTO;
import br.nite.newsly.service.ComentarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<ComentarioResponseDTO> criar(@Valid @RequestBody ComentarioRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComentarioResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<ComentarioResponseDTO>> buscarTodos() {
        return ResponseEntity.ok(service.buscarTodos());
    }

    /**
     * Endpoint adicional: busca comentários raiz de uma notícia específica.
     * GET /comentarios/noticia/{noticiaId}
     */
    @GetMapping("/noticia/{noticiaId}")
    public ResponseEntity<List<ComentarioResponseDTO>> buscarPorNoticia(@PathVariable Long noticiaId) {
        return ResponseEntity.ok(service.buscarPorNoticia(noticiaId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ComentarioResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ComentarioRequestDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
