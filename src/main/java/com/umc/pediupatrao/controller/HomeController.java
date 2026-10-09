package com.umc.pediupatrao.controller;

import com.umc.pediupatrao.service.PedidoService;
import com.umc.pediupatrao.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping({"/", "/home"})
    public String home(Model model, Authentication authentication) {
        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            model.addAttribute("content", "admin :: content");
        } else {
            model.addAttribute("content", "home :: content");
        }
        return "layout";
    }
    @GetMapping("/funcionarios")
    public String funcionarios(Model model) {
        model.addAttribute("content", "funcionarios :: content");
        return "layout";
    }

    @GetMapping("/entregadores")
    public String entregadores(Model model) {
        model.addAttribute("content", "entregadores :: content");
        return "layout";
    }

    @GetMapping("/horarios")
    public String horarios(Model model) {
        model.addAttribute("content", "horarios :: content");
        return "layout";
    }

    @GetMapping("/produtos")
    public String produtos(Model model) {
        model.addAttribute("content", "produtos :: content");
        return "layout";
    }

}