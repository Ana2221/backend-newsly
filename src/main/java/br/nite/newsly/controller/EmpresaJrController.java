package br.nite.newsly.controller;

import br.nite.newsly.dto.request.EmpresaJrRequestDTO;
import br.nite.newsly.dto.response.EmpresaJrResponseDTO;
import br.nite.newsly.service.EmpresaJrService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/empresas")
@RequiredArgsConstructor
public class EmpresaJrController {

    private final EmpresaJrService service;

    @PostMapping
    public ResponseEntity<EmpresaJrResponseDTO> criar(@Valid @RequestBody EmpresaJrRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @PutMapping("adicionar-membro/{idmembro}/{idempresa}")
    public void addmembro(@PathVariable Long idmembro, @PathVariable Long idempresa) {
        service.adicionarMembro(idmembro, idempresa);
    }
    @GetMapping("/{id}")
    public ResponseEntity<EmpresaJrResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<EmpresaJrResponseDTO>> buscarTodos() {
        return ResponseEntity.ok(service.buscarTodos());
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmpresaJrResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody EmpresaJrRequestDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
