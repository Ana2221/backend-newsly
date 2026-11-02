package br.nite.newsly.controller;

import br.nite.newsly.dto.request.DiretorRequestDTO;
import br.nite.newsly.dto.response.DiretorResponseDTO;
import br.nite.newsly.service.DiretorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/diretores")
@RequiredArgsConstructor
public class DiretorController {

    private final DiretorService service;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('RESPONSAVEL')")
    public ResponseEntity<DiretorResponseDTO> criar(@Valid @RequestBody DiretorRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RESPONSAVEL') or (hasAuthority('PERM_LEITOR') and !hasRole('RESPONSAVEL'))")
    public ResponseEntity<DiretorResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PERM_LEITOR') or hasRole('RESPONSAVEL')")
    public ResponseEntity<List<DiretorResponseDTO>> buscarTodos() {
        return ResponseEntity.ok(service.buscarTodos());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('RESPONSAVEL') and @securityAccess.isOwnDiretor(#p0))")
    public ResponseEntity<DiretorResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody DiretorRequestDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
