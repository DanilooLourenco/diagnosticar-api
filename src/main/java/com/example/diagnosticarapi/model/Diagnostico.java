package com.example.diagnosticarapi.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "diagnosticos")
@Getter
@Setter
public class Diagnostico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_hora")
    private LocalDateTime dataHora = LocalDateTime.now();

    @Column(name = "transcricao_audio", columnDefinition = "TEXT")
    private String transcricaoAudio;

    @Column(name = "relatorio_estruturado", columnDefinition = "TEXT")
    private String relatorioEstruturado;

    @ManyToOne
    @JoinColumn(name = "carro_id", nullable = false)
    @JsonManagedReference
    private Carro carro;

    // --- ADICIONE ESSAS LINHAS MANUAIS AQUI EMBAIXO: ---
    public Carro getCarro() {
        return carro;
    }

    public void setCarro(Carro carro) {
        // Correção de digitação: Garrett que a variável e o método usem "carro"
        this.carro = carro;
    }
}

