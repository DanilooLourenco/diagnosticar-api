package com.example.diagnosticarapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDTO {
    private Long id;
    private String nomeOficina;
    private String email;
    private String mensagem;
}