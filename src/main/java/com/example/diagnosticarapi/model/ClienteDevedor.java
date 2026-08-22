package com.example.diagnosticarapi.model; // 🚀 CORRIGIDO: Agora bate com o restante do projeto

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "clientes_devedores")
public class ClienteDevedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    private String apelido;

    @Column(nullable = false)
    private String documento; // CPF ou CNPJ

    private String telefone;

    @Column(name = "valor_original", nullable = false)
    private Double valorOriginal;

    @Column(name = "saldo_devedor", nullable = false)
    private Double saldoDevedor;

    @Column(name = "data_registro", nullable = false)
    private LocalDate dataRegistro;

    // Vincula a dívida diretamente ao serviço realizado específico
    @OneToOne
    @JoinColumn(name = "servico_realizado_id", referencedColumnName = "id")
    private ServicoRealizado servicoRealizado;

    // Vincula o devedor à oficina logada
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

}
