package br.nite.newsly.controller;

import br.nite.newsly.dto.request.ResponderSolicitacaoRequestDTO;
import br.nite.newsly.dto.request.SolicitacaoRequestDTO;
import br.nite.newsly.dto.response.SolicitacaoResponseDTO;
import br.nite.newsly.service.SolicitacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/solicitacoes")
@RequiredArgsConstructor
public class SolicitacaoController {

    private final SolicitacaoService service;

    @PostMapping
    public ResponseEntity<SolicitacaoResponseDTO> enviar(
            @Valid @RequestBody SolicitacaoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.enviar(dto));
    }

    @PatchMapping("/{id}/aceitar")
    public ResponseEntity<SolicitacaoResponseDTO> aceitar(@PathVariable Long id) {
        return ResponseEntity.ok(service.aceitar(id));
    }

    @PatchMapping("/{id}/recusar")
    public ResponseEntity<SolicitacaoResponseDTO> recusar(
            @PathVariable Long id,
            @Valid @RequestBody(required = false) ResponderSolicitacaoRequestDTO dto) {
        return ResponseEntity.ok(service.recusar(id, dto));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<SolicitacaoResponseDTO> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(service.cancelar(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SolicitacaoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<SolicitacaoResponseDTO>> buscarTodos() {
        return ResponseEntity.ok(service.buscarTodos());
    }

    @GetMapping("/responsavel/{responsavelId}")
    public ResponseEntity<List<SolicitacaoResponseDTO>> buscarPorResponsavel(
            @PathVariable Long responsavelId) {
        return ResponseEntity.ok(service.buscarPorResponsavel(responsavelId));
    }

    @GetMapping("/gestor/{gestorId}")
    public ResponseEntity<List<SolicitacaoResponseDTO>> buscarPorGestor(
            @PathVariable Long gestorId) {
        return ResponseEntity.ok(service.buscarPorGestor(gestorId));
    }

    @GetMapping("/gestor/{gestorId}/pendentes")
    public ResponseEntity<List<SolicitacaoResponseDTO>> buscarPendentesPorGestor(
            @PathVariable Long gestorId) {
        return ResponseEntity.ok(service.buscarPendentesPorGestor(gestorId));
    }
}
