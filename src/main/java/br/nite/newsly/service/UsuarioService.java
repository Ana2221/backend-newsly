package br.nite.newsly.service;

import br.nite.newsly.dto.request.LoginRequestDTO;
import br.nite.newsly.dto.request.UsuarioRequestDTO;
import br.nite.newsly.dto.response.DeduplicacaoResponseDTO;
import br.nite.newsly.dto.response.DeduplicacaoResponseDTO.UsuarioRemovidoDTO;
import br.nite.newsly.dto.response.UsuarioResponseDTO;
import br.nite.newsly.entity.Colaborador;
import br.nite.newsly.entity.Gestor;
import br.nite.newsly.entity.Responsavel;
import br.nite.newsly.entity.Usuario;
import br.nite.newsly.exception.BusinessException;
import br.nite.newsly.exception.ResourceNotFoundException;
import br.nite.newsly.mapper.UsuarioMapper;
import br.nite.newsly.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repository;
    private final UsuarioMapper mapper;

    @Transactional
    public UsuarioResponseDTO criar(UsuarioRequestDTO dto) {
        if (dto.contato() != null && dto.contato().email() != null
                && repository.existsByContatoEmail(dto.contato().email())) {
            throw new BusinessException("Já existe um usuário com o e-mail: " + dto.contato().email());
        }
        Usuario entity = mapper.toEntity(dto);
        return mapper.toResponse(repository.save(entity));
    }

    /**
     * Autentica o usuário pelo e-mail e senha.
     * Retorna a entidade bruta para que o OutputAdapter monte o response tipado.
     */
    @Transactional(readOnly = true)
    public Usuario login(LoginRequestDTO dto) {
        Usuario usuario = repository.findByContatoEmail(dto.email())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "e-mail", dto.email()));

        if (!usuario.getSenha().equals(dto.senha())) {
            throw new BusinessException("E-mail ou senha inválidos.");
        }

        return usuario;
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> buscarTodos() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    /**
     * Retorna as entidades brutas para uso pelo OutputAdapter.
     */
    @Transactional(readOnly = true)
    public List<Usuario> buscarTodasEntidades() {
        return repository.findAll();
    }

    @Transactional
    public UsuarioResponseDTO atualizar(Long id, UsuarioRequestDTO dto) {
        Usuario entity = buscarEntidade(id);
        mapper.updateEntity(dto, entity);
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    public void excluir(Long id) {
        buscarEntidade(id);
        repository.deleteById(id);
    }

    /**
     * Elimina duplicatas de usuários com base no nome (case-insensitive).
     *
     * <p>Critério de duplicata: dois ou mais usuários com o mesmo nome.
     * Critério de "mais recente": maior {@code id} (auto-increment — reflete
     * a ordem de inserção no banco).
     *
     * <p>Regra: o registro mais antigo (menor id) é mantido; os demais
     * (ids maiores, ou seja, os mais recentes) são excluídos.
     *
     * @return sumário com o total removido e os detalhes de cada exclusão.
     * @throws BusinessException se nenhuma duplicata for encontrada.
     */
    @Transactional
    public DeduplicacaoResponseDTO deduplicarUsuarios() {
        List<Usuario> duplicados = repository.findDuplicatesByNome();

        if (duplicados.isEmpty()) {
            throw new BusinessException("Nenhuma duplicata de usuário encontrada.");
        }

        List<UsuarioRemovidoDTO> removidos = new ArrayList<>();

        // A query retorna os grupos já ordenados por (nome ASC, id ASC).
        // Percorremos o resultado: o primeiro de cada grupo (menor id) é
        // o mais antigo — mantido. Todos os seguintes do mesmo grupo são
        // os mais recentes — excluídos.
        String nomeAtual = null;

        for (Usuario u : duplicados) {
            String nomeNormalizado = u.getNome().toLowerCase();

            if (!nomeNormalizado.equals(nomeAtual)) {
                // Novo grupo: este é o mais antigo (menor id) — manter.
                nomeAtual = nomeNormalizado;
                continue;
            }

            // Mesmo grupo, não é o primeiro: é mais recente — excluir.
            String email = (u.getContato() != null) ? u.getContato().getEmail() : null;
            String tipo  = resolverTipo(u);

            removidos.add(new UsuarioRemovidoDTO(u.getId(), u.getNome(), email, tipo));
            repository.deleteById(u.getId());
        }

        String mensagem = removidos.isEmpty()
                ? "Nenhum usuário removido."
                : removidos.size() + " usuário(s) duplicado(s) removido(s) com sucesso.";

        return new DeduplicacaoResponseDTO(removidos.size(), removidos, mensagem);
    }

    // ---------------------------------------------------------------------------
    // Helpers privados
    // ---------------------------------------------------------------------------

    /** Resolve o nome do tipo discriminado usando instanceof, igual ao OutputAdapter. */
    private String resolverTipo(Usuario u) {
        if (u instanceof Gestor)      return "GESTOR";
        if (u instanceof Colaborador) return "COLABORADOR";
        if (u instanceof Responsavel) return "RESPONSAVEL";
        return "USUARIO";
    }

    public Usuario buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", id));
    }
}
