package com.example.diagnosticarapi.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "servicos_realizados")
public class ServicoRealizado {

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "veiculo_placa")
    private String veiculoPlaca;

    @Column(name = "data_servico")
    private LocalDate dataServico;

    private String descricao;
    private double valor;

    // Construtores
    public ServicoRealizado() {}
    public ServicoRealizado(Long id, String veiculoPlaca, LocalDate dataServico, String descricao, double valor) {
        this.id = id;
        this.veiculoPlaca = veiculoPlaca;
        this.dataServico = dataServico;
        this.descricao = descricao;
        this.valor = valor;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getVeiculoPlaca() { return veiculoPlaca; }
    public void setVeiculoPlaca(String veiculoPlaca) { this.veiculoPlaca = veiculoPlaca; }
    public LocalDate getDataServico() { return dataServico; }
    public void setDataServico(LocalDate dataServico) { this.dataServico = dataServico; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    @Column(name = "forma_pagamento", nullable = false)
    private String formaPagamento;

    // 🚀 MÉTODOS GETTER E SETTER MANUAIS:
    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}

