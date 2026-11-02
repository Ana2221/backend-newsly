package br.nite.newsly.controller;

import br.nite.newsly.dto.request.FuncaoRequestDTO;
import br.nite.newsly.dto.response.FuncaoResponseDTO;
import br.nite.newsly.service.FuncaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/funcoes")
@RequiredArgsConstructor
public class FuncaoController {

    private final FuncaoService service;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('RESPONSAVEL')")
    public ResponseEntity<FuncaoResponseDTO> criar(@Valid @RequestBody FuncaoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RESPONSAVEL') or (hasAuthority('PERM_LEITOR') and !hasRole('RESPONSAVEL'))")
    public ResponseEntity<FuncaoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PERM_LEITOR') or hasRole('RESPONSAVEL')")
    public ResponseEntity<List<FuncaoResponseDTO>> buscarTodos() {
        return ResponseEntity.ok(service.buscarTodos());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('RESPONSAVEL') and @securityAccess.isOwnFuncao(#p0))")
    public ResponseEntity<FuncaoResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody FuncaoRequestDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
