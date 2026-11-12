package br.nite.newsly.controller;

import br.nite.newsly.dto.request.ResponderSolicitacaoRequestDTO;
import br.nite.newsly.dto.request.SolicitacaoRequestDTO;
import br.nite.newsly.dto.response.SolicitacaoResponseDTO;
import br.nite.newsly.service.SolicitacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/solicitacoes")
@RequiredArgsConstructor
public class SolicitacaoController {

    private final SolicitacaoService service;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('RESPONSAVEL') or hasRole('ADMIN')")
    public ResponseEntity<SolicitacaoResponseDTO> enviarJson(
            @Valid @RequestBody SolicitacaoRequestDTO dto,
            Authentication authentication) {
        return criarSolicitacao(dto, null, authentication);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('RESPONSAVEL') or hasRole('ADMIN')")
    public ResponseEntity<SolicitacaoResponseDTO> enviar(
            @Valid @ModelAttribute SolicitacaoRequestDTO dto,
            @RequestParam(value = "arquivo", required = false) MultipartFile arquivo,
            Authentication authentication) {
        return criarSolicitacao(dto, arquivo, authentication);
    }

    private ResponseEntity<SolicitacaoResponseDTO> criarSolicitacao(
            SolicitacaoRequestDTO dto, MultipartFile arquivo, Authentication authentication) {
        Long responsavelId = hasRole(authentication, "ROLE_ADMIN")
                ? dto.responsavelId()
                : Long.valueOf(authentication.getName());
        SolicitacaoRequestDTO request = new SolicitacaoRequestDTO(
                responsavelId,
                dto.titulo(),
                dto.conteudo(),
                dto.publicadaEm(),
                dto.empresaId());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.enviar(request, arquivo));
    }

    @PatchMapping("/{id}/aceitar")
    @PreAuthorize("hasRole('GESTOR') or hasRole('ADMIN')")
    public ResponseEntity<SolicitacaoResponseDTO> aceitar(
            @PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(service.aceitar(id, authentication.getName(), hasRole(authentication, "ROLE_ADMIN")));
    }

    @PatchMapping("/{id}/recusar")
    @PreAuthorize("hasRole('GESTOR') or hasRole('ADMIN')")
    public ResponseEntity<SolicitacaoResponseDTO> recusar(
            @PathVariable Long id,
            @Valid @RequestBody(required = false) ResponderSolicitacaoRequestDTO dto,
            Authentication authentication) {
        return ResponseEntity.ok(service.recusar(
                id, dto, authentication.getName(), hasRole(authentication, "ROLE_ADMIN")));
    }

    @PatchMapping("/{id}/cancelar")
    @PreAuthorize("hasRole('RESPONSAVEL') or hasRole('ADMIN')")
    public ResponseEntity<SolicitacaoResponseDTO> cancelar(
            @PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(service.cancelar(id, authentication.getName(), hasRole(authentication, "ROLE_ADMIN")));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('RESPONSAVEL') or hasRole('GESTOR') or hasRole('ADMIN')")
    public ResponseEntity<SolicitacaoResponseDTO> buscarPorId(
            @PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(service.buscarPorId(
                id,
                authentication.getName(),
                hasRole(authentication, "ROLE_ADMIN"),
                hasRole(authentication, "ROLE_GESTOR")));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SolicitacaoResponseDTO>> buscarTodos() {
        return ResponseEntity.ok(service.buscarTodos());
    }

    @GetMapping("/responsavel/{responsavelId}")
    @PreAuthorize("hasRole('RESPONSAVEL') or hasRole('ADMIN')")
    public ResponseEntity<List<SolicitacaoResponseDTO>> buscarPorResponsavel(
            @PathVariable Long responsavelId, Authentication authentication) {
        if (!hasRole(authentication, "ROLE_ADMIN")
                && !authentication.getName().equals(responsavelId.toString())) {
            throw new org.springframework.security.access.AccessDeniedException("Acesso não permitido.");
        }
        return ResponseEntity.ok(service.buscarPorResponsavel(responsavelId));
    }

    @GetMapping("/gestor/{gestorId}")
    @PreAuthorize("hasRole('GESTOR') or hasRole('ADMIN')")
    public ResponseEntity<List<SolicitacaoResponseDTO>> buscarPorGestor(
            @PathVariable Long gestorId, Authentication authentication) {
        if (!hasRole(authentication, "ROLE_ADMIN")
                && !authentication.getName().equals(gestorId.toString())) {
            throw new org.springframework.security.access.AccessDeniedException("Acesso não permitido.");
        }
        return ResponseEntity.ok(service.buscarPorGestor(gestorId));
    }

    @GetMapping("/gestor/{gestorId}/pendentes")
    @PreAuthorize("hasRole('GESTOR') or hasRole('ADMIN')")
    public ResponseEntity<List<SolicitacaoResponseDTO>> buscarPendentesPorGestor(
            @PathVariable Long gestorId, Authentication authentication) {
        if (!hasRole(authentication, "ROLE_ADMIN")
                && !authentication.getName().equals(gestorId.toString())) {
            throw new org.springframework.security.access.AccessDeniedException("Acesso não permitido.");
        }
        return ResponseEntity.ok(service.buscarPendentesParaGestor());
    }

    private boolean hasRole(Authentication authentication, String role) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(role));
    }
}
