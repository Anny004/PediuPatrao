package com.umc.pediupatrao.service;

import com.umc.pediupatrao.entity.ItemPedido;
import com.umc.pediupatrao.entity.Pedido;
import com.umc.pediupatrao.entity.Produto;
import com.umc.pediupatrao.repository.PedidoRepository;
import com.umc.pediupatrao.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    public Pedido criarPedido(Pedido pedido) {
        String usuarioAutenticado = SecurityContextHolder.getContext().getAuthentication().getName();
        pedido.setResponsavelEntrada(usuarioAutenticado);

        pedido.setDataHoraEntrada(LocalDateTime.now());

        if (pedido.getStatus() == null || pedido.getStatus().isEmpty()) {
            pedido.setStatus("RECEBIDO");
        }

        // define desconto inicial com 0
        if (pedido.getDesconto() == null) {
            pedido.setDesconto(0.0);
        }

        calcularValorTotal(pedido);

        return pedidoRepository.save(pedido);
    }

    public List<Pedido> listarPedidos() {
        return pedidoRepository.findAll();
    }

    public Pedido buscarPorId(String id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado com o ID: " + id));
    }

    public Pedido atualizarStatus(String id, String status) {
        Pedido pedido = buscarPorId(id);
        pedido.setStatus(status);
        return pedidoRepository.save(pedido);
    }

    public void calcularValorTotal(Pedido pedido) {
        double somaItens = 0.0;

        if (pedido.getItens() != null) {
            for (ItemPedido item : pedido.getItens()) {
                if (item.getProdutoId() != null) {
                    Produto produto = produtoRepository.findById(item.getProdutoId())
                            .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + item.getProdutoId()));

                    item.setNome(produto.getNome());
                    item.setPrecoUnitario(produto.getPreco());
                    somaItens += produto.getPreco() * item.getQuantidade();
                }
            }
        }

        double percentualDesconto = (pedido.getDesconto() != null) ? pedido.getDesconto() : 0.0;
        double valorDesconto = somaItens * (percentualDesconto / 100.0);
        double total = somaItens - valorDesconto;

        pedido.setValorTotal(Math.max(0.0, total));
    }
}