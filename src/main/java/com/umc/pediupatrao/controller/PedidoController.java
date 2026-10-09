package com.umc.pediupatrao.controller;

import com.umc.pediupatrao.entity.AlteracaoCampo;
import com.umc.pediupatrao.entity.ItemPedido;
import com.umc.pediupatrao.entity.Pedido;
import com.umc.pediupatrao.repository.PedidoRepository;
import com.umc.pediupatrao.service.AuditoriaService;
import com.umc.pediupatrao.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private AuditoriaService auditoriaService;

    @Autowired
    private ClienteService clienteService; // 👈 Serviço de clientes injetado

    private boolean verificarGerente(Authentication authentication) {
        if (authentication == null) return false;
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equalsIgnoreCase("ROLE_GERENTE")
                        || a.getAuthority().equalsIgnoreCase("GERENTE"));
    }

    private boolean verificarAtendente(Authentication authentication) {
        if (authentication == null) return false;
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equalsIgnoreCase("ROLE_ATENDENTE")
                        || a.getAuthority().equalsIgnoreCase("ATENDENTE"));
    }

    private boolean isTransicaoValida(String statusAtual, String novoStatus) {
        if (statusAtual == null) statusAtual = "RECEBIDO";

        String st = statusAtual.toUpperCase().trim();

        if ("FINALIZADO".equals(st) || "ENTREGUE".equals(st) || "CANCELADO".equals(st)) {
            return false;
        }

        switch (st) {
            case "RECEBIDO":
                return novoStatus.equalsIgnoreCase("EM PREPARAÇÃO") || novoStatus.equalsIgnoreCase("EM PREPARO") || novoStatus.equalsIgnoreCase("CANCELADO");
            case "EM PREPARAÇÃO":
            case "EM PREPARO":
                return novoStatus.equalsIgnoreCase("PRONTO") || novoStatus.equalsIgnoreCase("CANCELADO");
            case "PRONTO":
                return novoStatus.equalsIgnoreCase("SAIU PARA ENTREGA")
                        || novoStatus.equalsIgnoreCase("RETIRADO")
                        || novoStatus.equalsIgnoreCase("CANCELADO");
            case "SAIU PARA ENTREGA":
            case "RETIRADO":
                return novoStatus.equalsIgnoreCase("FINALIZADO");
            default:
                return false;
        }
    }

    @GetMapping
    public String listarPedidos(Model model, Authentication authentication) {
        boolean isGerente = verificarGerente(authentication);
        boolean isAtendente = verificarAtendente(authentication);

        model.addAttribute("pedidos", pedidoRepository.findAll());
        model.addAttribute("isGerente", isGerente);
        model.addAttribute("isAtendente", isAtendente);
        model.addAttribute("podeAlterarStatus", isGerente || isAtendente);
        model.addAttribute("content", "pedidos :: content");
        return "layout";
    }

    @GetMapping("/novo")
    public String novoPedido(Model model, Authentication authentication) {
        Pedido pedido = new Pedido();
        pedido.getItens().add(new ItemPedido());

        model.addAttribute("pedido", pedido);
        model.addAttribute("clientes", clienteService.listarClientes()); // 👈 Lista de clientes enviada para a view
        model.addAttribute("isGerente", verificarGerente(authentication));
        model.addAttribute("content", "novo-pedido :: content");
        return "layout";
    }

    @PostMapping("/salvar")
    public String salvarPedido(@ModelAttribute Pedido pedido,
                               @RequestParam(value = "nomeProduto", required = false) String nomeProduto,
                               @RequestParam(value = "quantidade", required = false, defaultValue = "1") Integer quantidade,
                               @RequestParam(value = "precoUnitario", required = false, defaultValue = "0.0") Double precoUnitario,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {

        boolean isGerente = verificarGerente(authentication);

        if (pedido.getDesconto() != null && pedido.getDesconto() > 0) {
            if (!isGerente) {
                pedido.setDesconto(0.0);
                redirectAttributes.addFlashAttribute("erro", "Aviso: Apenas GERENTE pode aplicar descontos. O desconto foi zerado.");
            } else if (pedido.getDesconto() > 20.0) {
                pedido.setDesconto(20.0);
                redirectAttributes.addFlashAttribute("erro", "Atenção: O desconto foi ajustado para o limite máximo permitido de 20%.");
            }
        } else if (pedido.getDesconto() == null) {
            pedido.setDesconto(0.0);
        }

        if (nomeProduto != null && !nomeProduto.isBlank()) {
            ItemPedido item = new ItemPedido();
            item.setNome(nomeProduto);
            item.setQuantidade(quantidade != null ? quantidade : 1);
            item.setPrecoUnitario(precoUnitario != null ? precoUnitario : 0.0);
            pedido.getItens().add(item);
        }

        double subtotal = pedido.getItens().stream()
                .mapToDouble(i -> (i.getPrecoUnitario() != null ? i.getPrecoUnitario() : 0.0) * i.getQuantidade())
                .sum();

        double percentualDesconto = pedido.getDesconto();
        double valorComDesconto = subtotal - (subtotal * (percentualDesconto / 100.0));

        if (percentualDesconto > 0) {
            pedido.setDescontoAnterior(0.0);
            pedido.setValorTotalAnteriorDesconto(subtotal);
            pedido.setUsuarioDesconto(authentication != null ? authentication.getName() : "SISTEMA");
            pedido.setDataHoraDesconto(LocalDateTime.now());
        }

        pedido.setValorTotal(Math.max(valorComDesconto, 0.0));
        pedido.setDataHoraEntrada(LocalDateTime.now());

        if (authentication != null) {
            pedido.setResponsavelEntrada(authentication.getName());
            pedido.setAtendente(authentication.getName());
        }

        if (pedido.getStatus() == null || pedido.getStatus().isBlank()) {
            pedido.setStatus("RECEBIDO");
        }

        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        List<AlteracaoCampo> camposAuditoria = new ArrayList<>();
        camposAuditoria.add(new AlteracaoCampo("valorTotal", "R$ 0.00", String.format("R$ %.2f", pedidoSalvo.getValorTotal())));
        camposAuditoria.add(new AlteracaoCampo("status", "-", pedidoSalvo.getStatus()));
        if (pedidoSalvo.getDesconto() > 0) {
            camposAuditoria.add(new AlteracaoCampo("desconto", "0%", pedidoSalvo.getDesconto() + "%"));
        }
        auditoriaService.registrar("INCLUSÃO DE PEDIDO", "PEDIDO", pedidoSalvo.getId(), camposAuditoria, null);

        redirectAttributes.addFlashAttribute("sucesso", "Pedido cadastrado com sucesso!");
        return "redirect:/pedidos";
    }

    @PostMapping("/aplicar-desconto")
    public String aplicarDesconto(@RequestParam("id") String id,
                                  @RequestParam("desconto") Double desconto,
                                  Authentication authentication,
                                  RedirectAttributes redirectAttributes) {

        boolean isGerente = verificarGerente(authentication);

        if (!isGerente) {
            redirectAttributes.addFlashAttribute("erro", "Acesso negado: Apenas o GERENTE pode aplicar descontos.");
            return "redirect:/pedidos";
        }

        Optional<Pedido> optionalPedido = pedidoRepository.findById(id);
        if (optionalPedido.isEmpty()) {
            redirectAttributes.addFlashAttribute("erro", "Pedido não encontrado.");
            return "redirect:/pedidos";
        }

        Pedido pedido = optionalPedido.get();
        String st = pedido.getStatus() != null ? pedido.getStatus().toUpperCase() : "";

        if ("SAIU PARA ENTREGA".equals(st) || "RETIRADO".equals(st) || "FINALIZADO".equals(st) || "ENTREGUE".equals(st) || "CANCELADO".equals(st)) {
            redirectAttributes.addFlashAttribute("erro", "Não é possível aplicar desconto: O pedido já saiu para entrega/retirada ou está finalizado/cancelado.");
            return "redirect:/pedidos";
        }

        if (desconto == null || desconto < 0) {
            desconto = 0.0;
        } else if (desconto > 20.0) {
            desconto = 20.0;
            redirectAttributes.addFlashAttribute("erro", "O desconto foi ajustado para o limite máximo permitido de 20%.");
        }

        double subtotal = pedido.getItens().stream()
                .mapToDouble(i -> (i.getPrecoUnitario() != null ? i.getPrecoUnitario() : 0.0) * i.getQuantidade())
                .sum();

        double descontoAnterior = pedido.getDesconto() != null ? pedido.getDesconto() : 0.0;
        double valorTotalAnterior = pedido.getValorTotal() != null ? pedido.getValorTotal() : 0.0;

        pedido.setDescontoAnterior(descontoAnterior);
        pedido.setValorTotalAnteriorDesconto(valorTotalAnterior);
        pedido.setUsuarioDesconto(authentication != null ? authentication.getName() : "SISTEMA");
        pedido.setDataHoraDesconto(LocalDateTime.now());

        pedido.setDesconto(desconto);
        double valorComDesconto = subtotal - (subtotal * (desconto / 100.0));
        pedido.setValorTotal(Math.max(valorComDesconto, 0.0));

        pedidoRepository.save(pedido);

        List<AlteracaoCampo> alteracoesDesconto = List.of(
                new AlteracaoCampo("desconto", descontoAnterior + "%", desconto + "%"),
                new AlteracaoCampo("valorTotal", String.format("R$ %.2f", valorTotalAnterior), String.format("R$ %.2f", pedido.getValorTotal()))
        );
        auditoriaService.registrar("CONCESSÃO DE DESCONTO", "PEDIDO", pedido.getId(), alteracoesDesconto, null);

        redirectAttributes.addFlashAttribute("sucesso", "Desconto de " + desconto + "% aplicado com sucesso ao pedido!");
        return "redirect:/pedidos";
    }

    @PostMapping("/atualizar-status")
    public String atualizarStatus(@RequestParam("id") String id,
                                  @RequestParam("novoStatus") String novoStatus,
                                  @RequestParam(value = "justificativa", required = false) String justificativa,
                                  Authentication authentication,
                                  RedirectAttributes redirectAttributes) {

        boolean isGerente = verificarGerente(authentication);
        boolean isAtendente = verificarAtendente(authentication);

        if (!isGerente && !isAtendente) {
            redirectAttributes.addFlashAttribute("erro", "Acesso negado: Perfil sem permissão para alterar o status.");
            return "redirect:/pedidos";
        }

        Optional<Pedido> optionalPedido = pedidoRepository.findById(id);

        if (optionalPedido.isEmpty()) {
            redirectAttributes.addFlashAttribute("erro", "Pedido não encontrado.");
            return "redirect:/pedidos";
        }

        Pedido pedido = optionalPedido.get();
        String statusAnterior = pedido.getStatus();

        if (!isTransicaoValida(statusAnterior, novoStatus)) {
            redirectAttributes.addFlashAttribute("erro",
                    "Transição inválida de status: Não é possível mudar de '" + statusAnterior + "' para '" + novoStatus + "'.");
            return "redirect:/pedidos";
        }

        if ("CANCELADO".equalsIgnoreCase(novoStatus)) {
            if (!isGerente) {
                redirectAttributes.addFlashAttribute("erro", "Acesso negado: Apenas o perfil GERENTE pode cancelar pedidos.");
                return "redirect:/pedidos";
            }

            if (justificativa == null || justificativa.isBlank()) {
                redirectAttributes.addFlashAttribute("erro", "O cancelamento exige uma justificativa.");
                return "redirect:/pedidos";
            }

            pedido.setStatusAnteriorCancelamento(statusAnterior);
            pedido.setUsuarioCancelamento(authentication != null ? authentication.getName() : "SISTEMA");
            pedido.setDataHoraCancelamento(LocalDateTime.now());
            pedido.setJustificativaCancelamento(justificativa);
        }

        if ("SAIU PARA ENTREGA".equalsIgnoreCase(novoStatus) || "RETIRADO".equalsIgnoreCase(novoStatus)) {
            pedido.setDataHoraSaida(LocalDateTime.now());
            if (authentication != null) {
                pedido.setResponsavelSaida(authentication.getName());
            }
        }

        pedido.setStatus(novoStatus);
        pedidoRepository.save(pedido);

        List<AlteracaoCampo> alteracoesStatus = List.of(
                new AlteracaoCampo("status", statusAnterior, novoStatus)
        );

        String tipoOperacao = "CANCELADO".equalsIgnoreCase(novoStatus) ? "CANCELAMENTO DE PEDIDO" : "MUDANÇA DE STATUS";
        String motivo = "CANCELADO".equalsIgnoreCase(novoStatus) ? justificativa : null;

        auditoriaService.registrar(tipoOperacao, "PEDIDO", pedido.getId(), alteracoesStatus, motivo);

        redirectAttributes.addFlashAttribute("sucesso", "Status alterado para: " + novoStatus);
        return "redirect:/pedidos";
    }
}