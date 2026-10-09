package com.umc.pediupatrao.controller;

import com.umc.pediupatrao.entity.Perfil;
import com.umc.pediupatrao.entity.Usuario;
import com.umc.pediupatrao.service.UsuarioService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuarios")
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioWebController {

    private final UsuarioService usuarioService;

    public UsuarioWebController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        model.addAttribute("content", "usuarios :: content");
        return "layout";
    }

    @PostMapping("/salvar")
    public String salvarUsuario(@RequestParam("username") String username,
                                @RequestParam("password") String password,
                                @RequestParam("role") String roleStr,
                                RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = new Usuario();
            usuario.setUsername(username);
            usuario.setPassword(password); // Passa a senha LIMPA, o Service que faz o encode
            usuario.setRole(Perfil.valueOf(roleStr.toUpperCase()));

            usuarioService.salvarUsuario(usuario);
            redirectAttributes.addFlashAttribute("sucesso", "Usuário '" + username + "' cadastrado com sucesso!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", "Perfil selecionado é inválido.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao cadastrar usuário: " + e.getMessage());
        }
        return "redirect:/usuarios";
    }

    @PostMapping("/alterar-perfil")
    public String alterarPerfil(@RequestParam("id") String id,
                                @RequestParam("perfil") String perfilStr,
                                RedirectAttributes redirectAttributes) {
        try {
            Perfil novoPerfil = Perfil.valueOf(perfilStr.toUpperCase());
            usuarioService.alterarPerfil(id, novoPerfil); // Usa o novo método dedicado

            redirectAttributes.addFlashAttribute("sucesso", "Perfil alterado para " + perfilStr + " com sucesso!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", "Perfil inválido: " + perfilStr);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao alterar perfil: " + e.getMessage());
        }
        return "redirect:/usuarios";
    }

    @PostMapping("/excluir/{id}")
    public String excluirUsuario(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.deletarUsuario(id);
            redirectAttributes.addFlashAttribute("sucesso", "Usuário excluído com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao excluir usuário.");
        }
        return "redirect:/usuarios";
    }
}