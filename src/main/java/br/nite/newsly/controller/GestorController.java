package br.nite.newsly.controller;

import br.nite.newsly.dto.request.GestorRequestDTO;
import br.nite.newsly.dto.response.DeduplicacaoResponseDTO;
import br.nite.newsly.dto.response.GestorResponseDTO;
import br.nite.newsly.service.GestorService;
import br.nite.newsly.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/gestores")
@RequiredArgsConstructor
public class GestorController {

    private final GestorService service;
    private final UsuarioService usuarioService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GestorResponseDTO> criar(@Valid @RequestBody GestorRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PERM_LEITOR') or hasRole('RESPONSAVEL') or hasRole('ADMIN')")
    public ResponseEntity<GestorResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PERM_LEITOR') or hasRole('RESPONSAVEL') or hasRole('ADMIN')")
    public ResponseEntity<List<GestorResponseDTO>> buscarTodos() {
        return ResponseEntity.ok(service.buscarTodos());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GestorResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody GestorRequestDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Remove duplicatas de usuários mantendo o registro mais antigo de cada grupo.
     *
     * <p>Critério de duplicata: mesmo nome (case-insensitive).
     * <br>Critério de "mais recente": maior {@code id} — os registros com id maior
     * são excluídos; o de menor id (mais antigo) é preservado.
     *
     * @return 200 com sumário dos usuários removidos, ou 409 se não há duplicatas.
     */
    @DeleteMapping("/deduplicar-usuarios")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DeduplicacaoResponseDTO> deduplicarUsuarios() {
        return ResponseEntity.ok(usuarioService.deduplicarUsuarios());
    }
}
