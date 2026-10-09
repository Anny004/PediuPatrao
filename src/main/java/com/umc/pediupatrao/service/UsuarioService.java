package com.umc.pediupatrao.service;

import com.umc.pediupatrao.entity.Perfil;
import com.umc.pediupatrao.entity.Usuario;
import com.umc.pediupatrao.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<Usuario> buscarPorId(String id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    // Criptografa a senha
    public void salvarUsuario(Usuario usuario) {
        if (usuario.getPassword() != null && !usuario.getPassword().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }
        usuarioRepository.save(usuario);
    }

    public void alterarPerfil(String id, Perfil novoPerfil) {
        usuarioRepository.findById(id).ifPresent(usuario -> {
            usuario.setRole(novoPerfil);
            usuarioRepository.save(usuario);
        });
    }

    public void deletarUsuario(String id) {
        usuarioRepository.deleteById(id);
    }

    public Iterable<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public void atualizarUsuario(String id, Usuario usuarioAtualizado) {
        usuarioRepository.findById(id).ifPresent(usuarioExistente -> {
            usuarioExistente.setUsername(usuarioAtualizado.getUsername());
            usuarioExistente.setRole(usuarioAtualizado.getRole());

            if (usuarioAtualizado.getPassword() != null && !usuarioAtualizado.getPassword().isBlank()) {
                usuarioExistente.setPassword(passwordEncoder.encode(usuarioAtualizado.getPassword()));
            }

            usuarioRepository.save(usuarioExistente);
        });
    }
}