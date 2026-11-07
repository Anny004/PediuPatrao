package com.umc.pediupatrao.controller;

import com.umc.pediupatrao.dto.ProdutoRankingDTO;
import com.umc.pediupatrao.dto.ResumoDashboardDTO;
import com.umc.pediupatrao.dto.StatusPedidoDTO;
import com.umc.pediupatrao.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardApiController {

    @Autowired
    private DashboardService dashboardService;

    // Auxiliar para processar os filtros de data enviados pelo Frontend (RF05)
    private LocalDateTime obterDataInicio(String filtro) {
        LocalDate hoje = LocalDate.now();
        return switch (filtro != null ? filtro : "hoje") {
            case "7dias" -> hoje.minusDays(7).atStartOfDay();
            case "30dias" -> hoje.minusDays(30).atStartOfDay();
            case "mes" -> hoje.withDayOfMonth(1).atStartOfDay();
            default -> hoje.atStartOfDay(); // "hoje"
        };
    }

    private LocalDateTime obterDataFim() {
        return LocalDate.now().atTime(LocalTime.MAX); // Até o fim do dia de hoje
    }

    @GetMapping("/resumo")
    public ResponseEntity<ResumoDashboardDTO> getResumo(@RequestParam(required = false, defaultValue = "hoje") String periodo) {
        return ResponseEntity.ok(dashboardService.obterResumo(obterDataInicio(periodo), obterDataFim()));
    }

    @GetMapping("/status")
    public ResponseEntity<List<StatusPedidoDTO>> getStatus(@RequestParam(required = false, defaultValue = "hoje") String periodo) {
        return ResponseEntity.ok(dashboardService.obterStatusPedidos(obterDataInicio(periodo), obterDataFim()));
    }

    @GetMapping("/ranking-produtos")
    public ResponseEntity<List<ProdutoRankingDTO>> getRankingProdutos(@RequestParam(required = false, defaultValue = "hoje") String periodo) {
        return ResponseEntity.ok(dashboardService.obterRankingProdutos(obterDataInicio(periodo), obterDataFim()));
    }
}
