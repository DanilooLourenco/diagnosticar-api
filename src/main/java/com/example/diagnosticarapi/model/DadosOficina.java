package com.example.diagnosticarapi.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Table;

@Entity
@Table(name = "dados_oficina")
public class DadosOficina {

    @Id
    private Integer id = 1; // Fixo em 1 para garantir registo único

    @Column(name = "nome_oficina", nullable = false)
    private String nomeOficina;

    private String cnpj;
    private String telefone;
    private String endereco;
    private String email;

    @Column(name = "mensagem_rodape")
    private String mensagemRodape;

    @Column(name = "logo_base64", columnDefinition = "LONGTEXT")
    private String logoBase64;

    // Construtores
    public DadosOficina() {}

    public DadosOficina(String nomeOficina, String cnpj, String telefone, String endereco, String email, String mensagemRodape, String logoBase64) {
        this.id = 1;
        this.nomeOficina = nomeOficina;
        this.cnpj = cnpj;
        this.telefone = telefone;
        this.endereco = endereco;
        this.email = email;
        this.mensagemRodape = mensagemRodape;
        this.logoBase64 = logoBase64;
    }

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNomeOficina() { return nomeOficina; }
    public void setNomeOficina(String nomeOficina) { this.nomeOficina = nomeOficina; }

    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMensagemRodape() { return mensagemRodape; }
    public void setMensagemRodape(String mensagemRodape) { this.mensagemRodape = mensagemRodape; }

    public String getLogoBase64() { return logoBase64; }
    public void setLogoBase64(String logoBase64) { this.logoBase64 = logoBase64; }
}