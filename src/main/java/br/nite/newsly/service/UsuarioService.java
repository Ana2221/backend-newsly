package br.nite.newsly.service;

import br.nite.newsly.dto.request.LoginRequestDTO;
import br.nite.newsly.dto.request.UsuarioRequestDTO;
import br.nite.newsly.dto.response.UsuarioResponseDTO;
import br.nite.newsly.entity.Usuario;
import br.nite.newsly.exception.BusinessException;
import br.nite.newsly.exception.ResourceNotFoundException;
import br.nite.newsly.mapper.UsuarioMapper;
import br.nite.newsly.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public Usuario buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", id));
    }
}
