package com.example.diagnosticarapi.controller;

import com.example.diagnosticarapi.dto.LoginResponseDTO;
import com.example.diagnosticarapi.model.Usuario;
import com.example.diagnosticarapi.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioRepository repository;

    // 🚀 Rota para cadastrar uma nova oficina
    @PostMapping("/cadastrar")
    public ResponseEntity<?> cadastrar(@RequestBody Usuario usuario) {
        if (repository.existsByEmail(usuario.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"erro\": \"Este e-mail já está cadastrado!\"}");
        }
        Usuario novoUsuario = repository.save(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoUsuario);
    }

    // 🚀 Rota para realizar o Login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Usuario loginData) {
        Optional<Usuario> usuarioOpt = repository.findByEmail(loginData.getEmail());

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            // Compara a senha digitada
            if (usuario.getSenha().equals(loginData.getSenha())) {
                LoginResponseDTO response = new LoginResponseDTO(
                        usuario.getId(),
                        usuario.getNomeOficina(),
                        usuario.getEmail(),
                        "Login realizado com sucesso!"
                );
                return ResponseEntity.ok(response);
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("{\"erro\": \"E-mail ou senha incorretos!\"}");
    }
}