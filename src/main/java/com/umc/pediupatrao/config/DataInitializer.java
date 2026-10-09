package com.umc.pediupatrao.config;

import com.umc.pediupatrao.entity.Perfil;
import com.umc.pediupatrao.entity.Usuario;
import com.umc.pediupatrao.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initDatabase(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Atualiza ou cria os 3 usuários garantindo a senha '123456' e os perfis corretos
            salvarOuAtualizarUsuario(usuarioRepository, passwordEncoder, "admin", "123456", Perfil.ADMIN);
            salvarOuAtualizarUsuario(usuarioRepository, passwordEncoder, "gerente", "123456", Perfil.GERENTE);
            salvarOuAtualizarUsuario(usuarioRepository, passwordEncoder, "atendente", "123456", Perfil.ATENDENTE);
        };
    }

    private void salvarOuAtualizarUsuario(UsuarioRepository repository, PasswordEncoder encoder, String username, String password, Perfil role) {
        Usuario usuario = repository.findByUsername(username).orElse(new Usuario());
        usuario.setUsername(username);
        usuario.setPassword(encoder.encode(password));
        usuario.setRole(role);
        repository.save(usuario);
        System.out.println("Usuário " + username + " atualizado/criado com sucesso! Role: " + role);
    }
}