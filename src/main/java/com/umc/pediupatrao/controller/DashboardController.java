package com.umc.pediupatrao.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String exibirDashboard() {
        // Retorna o template HTML onde está o dashboard
        return "admin"; // Se o seu arquivo for admin.html e estiver na raiz de templates
    }
}