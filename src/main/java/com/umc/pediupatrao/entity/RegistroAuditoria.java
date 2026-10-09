package com.umc.pediupatrao.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "auditoria")
public class RegistroAuditoria {

    @Id
    private String id;
    private String usuario;
    private String perfil;
    private LocalDateTime dataHora;
    private String tipoOperacao;
    private String entidadeAfetada;
    private String entidadeId;
    private List<AlteracaoCampo> alteracoes;
    private String justificativa;

    public RegistroAuditoria() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public String getPerfil() { return perfil; }
    public void setPerfil(String perfil) { this.perfil = perfil; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
    public String getTipoOperacao() { return tipoOperacao; }
    public void setTipoOperacao(String tipoOperacao) { this.tipoOperacao = tipoOperacao; }
    public String getEntidadeAfetada() { return entidadeAfetada; }
    public void setEntidadeAfetada(String entidadeAfetada) { this.entidadeAfetada = entidadeAfetada; }
    public String getEntidadeId() { return entidadeId; }
    public void setEntidadeId(String entidadeId) { this.entidadeId = entidadeId; }
    public List<AlteracaoCampo> getAlteracoes() { return alteracoes; }
    public void setAlteracoes(List<AlteracaoCampo> alteracoes) { this.alteracoes = alteracoes; }
    public String getJustificativa() { return justificativa; }
    public void setJustificativa(String justificativa) { this.justificativa = justificativa; }
}