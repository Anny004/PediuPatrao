package com.umc.pediupatrao.controller;

import com.umc.pediupatrao.service.AuditoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auditoria")
@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
public class AuditoriaController {

    @Autowired
    private AuditoriaService auditoriaService;

    private boolean verificarPermissoes(Authentication authentication) {
        if (authentication == null) return false;
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equalsIgnoreCase("ROLE_ADMIN")
                        || a.getAuthority().equalsIgnoreCase("ADMIN")
                        || a.getAuthority().equalsIgnoreCase("ROLE_GERENTE")
                        || a.getAuthority().equalsIgnoreCase("GERENTE"));
    }

    @GetMapping
    public String listarLogs(Model model, Authentication authentication) {
        boolean podeAcessar = verificarPermissoes(authentication);

        model.addAttribute("logs", auditoriaService.listarTodos());
        model.addAttribute("isGerenteOuAdmin", podeAcessar);
        model.addAttribute("content", "auditoria :: content");

        return "layout";
    }
}