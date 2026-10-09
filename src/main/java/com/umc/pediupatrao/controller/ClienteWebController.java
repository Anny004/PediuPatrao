package com.umc.pediupatrao.controller;

import com.umc.pediupatrao.entity.AlteracaoCampo;
import com.umc.pediupatrao.entity.Cliente;
import com.umc.pediupatrao.service.AuditoriaService;
import com.umc.pediupatrao.service.ClienteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/clientes")
public class ClienteWebController {

    private final ClienteService clienteService;
    private final AuditoriaService auditoriaService;

    public ClienteWebController(ClienteService clienteService, AuditoriaService auditoriaService) {
        this.clienteService = clienteService;
        this.auditoriaService = auditoriaService;
    }

    @GetMapping
    public String listarClientes(Model model) {
        model.addAttribute("clientes", clienteService.listarClientes());
        model.addAttribute("content", "clientes :: content");
        return "layout";
    }

    @PostMapping("/salvar")
    public String salvarCliente(@ModelAttribute Cliente cliente, RedirectAttributes redirectAttributes) {
        Cliente salvo = clienteService.salvar(cliente);

        List<AlteracaoCampo> alteracoes = new ArrayList<>();
        if (salvo.getNome() != null) alteracoes.add(new AlteracaoCampo("nome", "-", salvo.getNome()));
        if (salvo.getLogradouro() != null) alteracoes.add(new AlteracaoCampo("logradouro", "-", salvo.getLogradouro()));
        if (salvo.getTelefone() != null) alteracoes.add(new AlteracaoCampo("telefone", "-", salvo.getTelefone()));

        auditoriaService.registrar("INCLUSÃO DE CLIENTE", "CLIENTE", salvo.getId(), alteracoes, null);

        redirectAttributes.addFlashAttribute("sucesso", "Cliente cadastrado com sucesso!");
        return "redirect:/clientes";
    }

    @PostMapping("/atualizar")
    public String atualizarCliente(@RequestParam("id") String id,
                                   @RequestParam("nome") String nome,
                                   @RequestParam("telefone") String telefone,
                                   @RequestParam("logradouro") String logradouro,
                                   RedirectAttributes redirectAttributes) {

        Optional<Cliente> opt = clienteService.listarClientes().stream()
                .filter(c -> id.equals(c.getId()))
                .findFirst();

        List<AlteracaoCampo> alteracoes = new ArrayList<>();

        if (opt.isPresent()) {
            Cliente antigo = opt.get();

            if (!Objects.equals(antigo.getLogradouro(), logradouro)) {
                alteracoes.add(new AlteracaoCampo("logradouro", antigo.getLogradouro(), logradouro));
            }
            if (!Objects.equals(antigo.getTelefone(), telefone)) {
                alteracoes.add(new AlteracaoCampo("telefone", antigo.getTelefone(), telefone));
            }
            if (!Objects.equals(antigo.getNome(), nome)) {
                alteracoes.add(new AlteracaoCampo("nome", antigo.getNome(), nome));
            }

            antigo.setNome(nome);
            antigo.setTelefone(telefone);
            antigo.setLogradouro(logradouro);
            clienteService.salvar(antigo);

            if (!alteracoes.isEmpty()) {
                auditoriaService.registrar("ALTERAÇÃO DE CLIENTE", "CLIENTE", id, alteracoes, null);
            }

            redirectAttributes.addFlashAttribute("sucesso", "Dados do cliente atualizados e auditados!");
        } else {
            redirectAttributes.addFlashAttribute("erro", "Cliente não encontrado.");
        }

        return "redirect:/clientes";
    }
}