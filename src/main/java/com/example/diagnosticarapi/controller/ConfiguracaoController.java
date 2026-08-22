package com.example.diagnosticarapi.controller;

import com.example.diagnosticarapi.model.DadosOficina;
import com.example.diagnosticarapi.repository.DadosOficinaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/configuracoes")
@CrossOrigin(origins = "*")
public class ConfiguracaoController {

    @Autowired
    private DadosOficinaRepository oficinaRepository;

    // 🔍 ROTA PARA BUSCAR OS DADOS DA OFICINA (GET)
    @GetMapping("/oficina")
    public ResponseEntity<?> obterDadosOficina() {
        try {
            // Como fixamos o ID em 1, procuramos sempre pelo registro 1
            Optional<DadosOficina> dadosOpt = oficinaRepository.findById(1);

            if (dadosOpt.isPresent()) {
                return ResponseEntity.ok(dadosOpt.get());
            } else {
                // Caso não encontre por algum motivo, devolve um objeto vazio padrão
                DadosOficina padrao = new DadosOficina();
                padrao.setNomeOficina("Configurar Nome da Oficina");
                return ResponseEntity.ok(padrao);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"erro\": \"" + e.getMessage() + "\"}");
        }
    }

    // 💾 ROTA PARA SALVAR / ATUALIZAR OS DADOS DA OFICINA (POST)
    @PostMapping("/oficina")
    public ResponseEntity<?> salvarDadosOficina(@RequestBody DadosOficina novosDados) {
        try {
            // Garante que o ID será sempre 1 para apenas atualizar o mesmo registro
            novosDados.setId(1);

            DadosOficina dadosSalvos = oficinaRepository.save(novosDados);
            return ResponseEntity.ok(dadosSalvos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"erro\": \"Erro ao salvar configurações: " + e.getMessage() + "\"}");
        }
    }
}