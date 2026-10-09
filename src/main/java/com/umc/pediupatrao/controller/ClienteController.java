package com.umc.pediupatrao.controller;

import com.umc.pediupatrao.entity.AlteracaoCampo;
import com.umc.pediupatrao.entity.Cliente;
import com.umc.pediupatrao.service.AuditoriaService;
import com.umc.pediupatrao.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private AuditoriaService auditoriaService;

    @GetMapping
    public List<Cliente> listarClientes() {
        return clienteService.listarClientes();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirCliente(@PathVariable String id) {
        clienteService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<Cliente> salvarCliente(@RequestBody Cliente cliente) {
        Cliente salvo = clienteService.salvar(cliente);

        List<AlteracaoCampo> alteracoes = new ArrayList<>();
        if (salvo.getNome() != null) alteracoes.add(new AlteracaoCampo("nome", "-", salvo.getNome()));
        if (salvo.getLogradouro() != null) alteracoes.add(new AlteracaoCampo("logradouro", "-", salvo.getLogradouro()));
        if (salvo.getTelefone() != null) alteracoes.add(new AlteracaoCampo("telefone", "-", salvo.getTelefone()));

        auditoriaService.registrar("INCLUSÃO DE CLIENTE", "CLIENTE", salvo.getId(), alteracoes, null);

        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> atualizarCliente(@PathVariable String id, @RequestBody Cliente cliente) {
        Optional<Cliente> clienteAntigoOpt = clienteService.listarClientes().stream()
                .filter(c -> id.equals(c.getId()))
                .findFirst();

        List<AlteracaoCampo> alteracoes = new ArrayList<>();

        if (clienteAntigoOpt.isPresent()) {
            Cliente antigo = clienteAntigoOpt.get();

            if (!Objects.equals(antigo.getLogradouro(), cliente.getLogradouro())) {
                alteracoes.add(new AlteracaoCampo("logradouro", antigo.getLogradouro(), cliente.getLogradouro()));
            }
            if (!Objects.equals(antigo.getTelefone(), cliente.getTelefone())) {
                alteracoes.add(new AlteracaoCampo("telefone", antigo.getTelefone(), cliente.getTelefone()));
            }
            if (!Objects.equals(antigo.getNome(), cliente.getNome())) {
                alteracoes.add(new AlteracaoCampo("nome", antigo.getNome(), cliente.getNome()));
            }
        }

        cliente.setId(id);
        Cliente atualizado = clienteService.salvar(cliente);

        if (!alteracoes.isEmpty()) {
            auditoriaService.registrar("ALTERAÇÃO DE CLIENTE", "CLIENTE", id, alteracoes, null);
        }

        return ResponseEntity.ok(atualizado);
    }
}