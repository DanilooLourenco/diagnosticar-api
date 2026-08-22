package com.example.diagnosticarapi.controller;

import com.example.diagnosticarapi.model.ClienteDevedor;
import com.example.diagnosticarapi.repository.ClienteDevedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/devedores")
@CrossOrigin(origins = "*") // 🚀 Permite que o seu HTML acesse o Java sem bloqueios
public class ClienteDevedorController {

    @Autowired
    private ClienteDevedorRepository repository;

    // 🚀 Rota que o JavaScript vai chamar: GET http://localhost:8080/api/devedores/busca?termo=...
    @GetMapping("/busca")
    public ResponseEntity<ClienteDevedor> buscarDevedor(
            @RequestParam("termo") String termo,
            @RequestParam("usuarioId") Long usuarioId) {

        return repository.findByTermoEUsuario(termo, usuarioId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/pagar")
    public ResponseEntity<?> registrarPagamento(@PathVariable("id") Long id, @RequestParam("valorPago") Double valorPago) {
        return repository.findById(id).map(devedor -> {
            double novoSaldo = devedor.getSaldoDevedor() - valorPago;

            if (novoSaldo <= 0) {
                // Se quitou tudo, apaga a dívida do banco
                repository.delete(devedor);
                return ResponseEntity.ok().body("{\"status\": \"QUITADO\", \"mensagem\": \"Dívida quitada com sucesso!\"}");
            } else {
                // Se pagou só uma parte, atualiza o saldo
                devedor.setSaldoDevedor(novoSaldo);
                repository.save(devedor);
                return ResponseEntity.ok().body("{\"status\": \"PARCIAL\", \"novoSaldo\": " + novoSaldo + "}");
            }
        }).orElse(ResponseEntity.notFound().build());
    }
}