package com.umc.pediupatrao.dto;

public record ResumoDashboardDTO(
        long quantidadePedidos,
        double faturamentoTotal,
        double ticketMedio
) {}