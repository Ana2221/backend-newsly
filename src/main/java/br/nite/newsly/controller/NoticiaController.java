package br.nite.newsly.controller;

import br.nite.newsly.dto.request.NoticiaRequestDTO;
import br.nite.newsly.dto.response.NoticiaResponseDTO;
import br.nite.newsly.service.NoticiaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<NoticiaResponseDTO> criar(@Valid @RequestBody NoticiaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @PatchMapping("/{id}/imagem")
    public ResponseEntity<NoticiaResponseDTO> uploadImagem(
            @PathVariable Long id,
            @RequestParam("arquivo") MultipartFile arquivo) {
        return ResponseEntity.ok(service.uploadImagem(id, arquivo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoticiaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<NoticiaResponseDTO>> buscarTodos() {
        return ResponseEntity.ok(service.buscarTodos());
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoticiaResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody NoticiaRequestDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
