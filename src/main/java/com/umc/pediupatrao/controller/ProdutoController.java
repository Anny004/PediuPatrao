package com.umc.pediupatrao.controller;

import com.umc.pediupatrao.entity.Produto;
import com.umc.pediupatrao.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/produtos") // Removido o /api para bater com o HTML
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    @GetMapping
    public String listarProdutos(Model model) {
        model.addAttribute("produtos", produtoService.listarTodos());
        model.addAttribute("content", "produtos :: content");
        return "layout";
    }

    @PostMapping("/salvar")
    public String salvarProduto(@ModelAttribute Produto produto) {
        // Como você criou o campo 'ativo', vamos garantir que o produto nasça ativo
        if(produto.getAtivo() == null) {
            produto.setAtivo(true);
        }
        produtoService.salvar(produto);
        return "redirect:/produtos";
    }

    @PostMapping("/atualizar")
    public String atualizarProduto(@ModelAttribute Produto produto) {
        if(produto.getAtivo() == null) {
            produto.setAtivo(true);
        }
        produtoService.salvar(produto);
        return "redirect:/produtos";
    }

    // Exclusão via fetch do JavaScript
    @ResponseBody
    @PreAuthorize("hasAnyAuthority('ROLE_GERENTE', 'GERENTE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirProduto(@PathVariable String id) {
        produtoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}