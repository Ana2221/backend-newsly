package br.nite.newsly.controller;

import br.nite.newsly.dto.request.CriarUsuarioRequestDTO;
import br.nite.newsly.dto.request.LoginRequestDTO;
import br.nite.newsly.dto.request.UsuarioRequestDTO;
import br.nite.newsly.dto.response.CriarUsuarioResponseDTO;
import br.nite.newsly.dto.response.UsuarioTipadoResponse;
import br.nite.newsly.service.UsuarioAdapterService;
import br.nite.newsly.service.UsuarioOutputAdapterService;
import br.nite.newsly.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioAdapterService adapterService;
    private final UsuarioOutputAdapterService outputAdapter;

    /**
     * Cria um usuário do tipo informado no campo "tipo".
     * Tipos disponíveis: USUARIO, COLABORADOR, GESTOR, RESPONSAVEL
     */
    @PostMapping
    public ResponseEntity<CriarUsuarioResponseDTO> criar(
            @Valid @RequestBody CriarUsuarioRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adapterService.criar(dto));
    }

    /**
     * Autentica o usuário e retorna o DTO do tipo real:
     * ColaboradorResponseDTO, GestorResponseDTO ou ResponsavelResponseDTO.
     */
    @PostMapping("/login")
    public ResponseEntity<UsuarioTipadoResponse> login(
            @Valid @RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(outputAdapter.adaptarParaLogin(usuarioService.login(dto)));
    }

    /**
     * Retorna o usuário com os dados completos do seu tipo real.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CriarUsuarioResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(outputAdapter.adaptar(usuarioService.buscarEntidade(id)));
    }

    /**
     * Retorna todos os usuários com os dados completos de cada tipo real.
     */
    @GetMapping
    public ResponseEntity<List<CriarUsuarioResponseDTO>> buscarTodos() {
        List<CriarUsuarioResponseDTO> response = usuarioService.buscarTodasEntidades()
                .stream()
                .map(outputAdapter::adaptar)
                .toList();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CriarUsuarioResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioRequestDTO dto) {
        usuarioService.atualizar(id, dto);
        return ResponseEntity.ok(outputAdapter.adaptar(usuarioService.buscarEntidade(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        usuarioService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
