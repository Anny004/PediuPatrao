package com.umc.pediupatrao.service;
import com.umc.pediupatrao.dto.ProdutoRankingDTO;
import com.umc.pediupatrao.dto.ResumoDashboardDTO;
import com.umc.pediupatrao.dto.StatusPedidoDTO;
import com.umc.pediupatrao.entity.Pedido;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DashboardService {

    @Autowired
    private MongoTemplate mongoTemplate;

    // RF01: Indicadores gerais de vendas
    public ResumoDashboardDTO obterResumo(LocalDateTime inicio, LocalDateTime fim) {
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("dataHoraEntrada").gte(inicio).lte(fim)),
                Aggregation.group().count().as("quantidadePedidos")
                        .sum("valorTotal").as("faturamentoTotal")
        );

        AggregationResults<org.bson.Document> results = mongoTemplate.aggregate(agg, Pedido.class, org.bson.Document.class);
        org.bson.Document doc = results.getUniqueMappedResult();

        if (doc == null) {
            return new ResumoDashboardDTO(0, 0.0, 0.0);
        }

        long qtd = doc.getInteger("quantidadePedidos", 0);
        double faturamento = doc.getDouble("faturamentoTotal") != null ? doc.getDouble("faturamentoTotal") : 0.0;
        double ticketMedio = qtd > 0 ? faturamento / qtd : 0.0;

        return new ResumoDashboardDTO(qtd, faturamento, ticketMedio);
    }

    // RF03: Situação dos pedidos
    public List<StatusPedidoDTO> obterStatusPedidos(LocalDateTime inicio, LocalDateTime fim) {
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("dataHoraEntrada").gte(inicio).lte(fim)),
                Aggregation.group("status").count().as("quantidade"),
                Aggregation.project("quantidade").and("_id").as("status")
        );

        return mongoTemplate.aggregate(agg, Pedido.class, StatusPedidoDTO.class).getMappedResults();
    }

    // RF04: Produtos mais vendidos
    public List<ProdutoRankingDTO> obterRankingProdutos(LocalDateTime inicio, LocalDateTime fim) {
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("dataHoraEntrada").gte(inicio).lte(fim)),
                Aggregation.unwind("itens"), // Desmembra a lista de itens
                Aggregation.group("itens.nome").sum("itens.quantidade").as("quantidadeVendida"),
                Aggregation.project("quantidadeVendida").and("_id").as("nomeProduto"),
                Aggregation.sort(Sort.Direction.DESC, "quantidadeVendida"),
                Aggregation.limit(5) // Top 5
        );

        return mongoTemplate.aggregate(agg, Pedido.class, ProdutoRankingDTO.class).getMappedResults();
    }
}