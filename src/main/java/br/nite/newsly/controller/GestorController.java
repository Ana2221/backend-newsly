package br.nite.newsly.controller;

import br.nite.newsly.dto.request.GestorRequestDTO;
import br.nite.newsly.dto.response.GestorResponseDTO;
import br.nite.newsly.service.GestorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/gestores")
@RequiredArgsConstructor
public class GestorController {

    private final GestorService service;

    @PostMapping
    public ResponseEntity<GestorResponseDTO> criar(@Valid @RequestBody GestorRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GestorResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<GestorResponseDTO>> buscarTodos() {
        return ResponseEntity.ok(service.buscarTodos());
    }

    @PutMapping("/{id}")
    public ResponseEntity<GestorResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody GestorRequestDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
