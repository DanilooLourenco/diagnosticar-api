package com.example.diagnosticarapi.controller;

import com.example.diagnosticarapi.model.Veiculo;
import com.example.diagnosticarapi.model.ServicoRealizado;
import com.example.diagnosticarapi.repository.VeiculoRepository;
import com.example.diagnosticarapi.repository.ServicoRealizadoRepository;
import com.example.diagnosticarapi.model.Carro;
import com.example.diagnosticarapi.model.Diagnostico;
import com.example.diagnosticarapi.model.ClienteDevedor;
import com.example.diagnosticarapi.model.Usuario; // ➕ IMPORTADO PARA VINCULAR A OFICINA
import com.example.diagnosticarapi.repository.CarroRepository;
import com.example.diagnosticarapi.repository.DiagnosticoRepository;
import com.example.diagnosticarapi.repository.ClienteDevedorRepository;
import com.example.diagnosticarapi.service.OpenAiServiceCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/diagnosticos")
@CrossOrigin("*")
public class DiagnosticoController {

    @Autowired
    private DiagnosticoRepository repository;

    @Autowired
    private CarroRepository carroRepository;

    @Autowired
    private OpenAiServiceCustom openAiService;

    @Autowired
    private VeiculoRepository veiculoRepository;

    @Autowired
    private ServicoRealizadoRepository servicoRealizadoRepository;

    @Autowired
    private ClienteDevedorRepository clienteDevedorRepository;

    // ==========================================================================
    // 1. Rota para buscar o prontuário completo de um carro pela placa
    // ==========================================================================
    @CrossOrigin(origins = "*")
    @GetMapping("/placa/{placa}")
    public List<Diagnostico> buscarPorPlaca(@PathVariable String placa) {
        return repository.findByCarroPlaca(placa);
    }

    // ==========================================================================
    // 2. Rota para listar todos os diagnósticos já salvos no MySQL
    // ==========================================================================
    @CrossOrigin(origins = "*")
    @GetMapping
    public List<Diagnostico> listarTodos() {
        return repository.findAll();
    }

    // ==========================================================================
    // 3. Método principal: Recebe o áudio gravado e a placa (IA e Diagnósticos)
    // ==========================================================================
    @PostMapping("/audio")
    public Diagnostico receberAudio(
            @RequestParam("audio") MultipartFile multipartFile,
            @RequestParam(value = "placa", required = false) String placa) {

        Carro carro = null;

        if (placa != null && !placa.trim().isEmpty()) {
            carro = carroRepository.findByPlaca(placa)
                    .orElseGet(() -> {
                        Carro novoCarro = new Carro();
                        novoCarro.setPlaca(placa);
                        novoCarro.setMarca("Detectado");
                        novoCarro.setModelo("Pela Placa");
                        novoCarro.setAno(2026);
                        return carroRepository.save(novoCarro);
                    });
        }

        try {
            File arquivoTemporario = new File(System.getProperty("java.io.tmpdir") + "/" + multipartFile.getOriginalFilename());
            try (FileOutputStream fos = new FileOutputStream(arquivoTemporario)) {
                fos.write(multipartFile.getBytes());
            }

            String textoTranscrito = openAiService.transcreverAudio(arquivoTemporario);
            String relatorioIa = openAiService.analisarTexto(textoTranscrito);

            Diagnostico novoDiagnostico = new Diagnostico();
            novoDiagnostico.setDataHora(LocalDateTime.now());
            novoDiagnostico.setTranscricaoAudio(textoTranscrito);
            novoDiagnostico.setRelatorioEstruturado(relatorioIa);

            arquivoTemporario.delete();

            if (carro != null) {
                novoDiagnostico.setCarro(carro);
                return repository.save(novoDiagnostico);
            }

            return novoDiagnostico;

        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar o áudio na IA: " + e.getMessage());
        }
    }

    // ==========================================================================
    // ➕ ROTA PARA SALVAR UM NOVO VEÍCULO E SERVIÇO (POST) - CORRIGIDA 🚀
    // ==========================================================================
    @CrossOrigin(origins = "*")
    @PostMapping("/novo")
    public ResponseEntity<?> cadastrarNovoServico(@RequestBody java.util.Map<String, Object> payload) {
        try {
            String placa = ((String) payload.get("placa")).trim().toUpperCase();
            String modelo = (String) payload.get("modelo");
            int ano = Integer.parseInt(payload.get("ano").toString());
            String descricao = (String) payload.get("descricao");
            double valor = Double.parseDouble(payload.get("valor").toString());

            // Captura a nova informação da forma de pagamento que vem do front
            String formaPagamento = payload.get("formaPagamento") != null ? payload.get("formaPagamento").toString() : "A_VISTA";

            // 🚀 1. EXTRAI A OFICINA / USUÁRIO LOGADO DO PAYLOAD
            Usuario usuarioLogado = null;
            if (payload.get("usuario") != null) {
                Object usuarioObj = payload.get("usuario");
                if (usuarioObj instanceof java.util.Map) {
                    Object idObj = ((java.util.Map<?, ?>) usuarioObj).get("id");
                    if (idObj != null) {
                        Long uId = Long.parseLong(idObj.toString());
                        usuarioLogado = new Usuario();
                        usuarioLogado.setId(uId);
                    }
                }
            } else if (payload.get("usuarioId") != null) {
                Long uId = Long.parseLong(payload.get("usuarioId").toString());
                usuarioLogado = new Usuario();
                usuarioLogado.setId(uId);
            }

            // 2. Verifica se o veículo já existe no banco, se não existir, cadastra
            if (!veiculoRepository.existsById(placa)) {
                Veiculo novoVeiculo = new Veiculo(placa, modelo, ano);
                veiculoRepository.save(novoVeiculo);
            }

            // 3. Cria e salva o registro do serviço realizado atrelado à placa e à OFICINA
            ServicoRealizado novoServico = new ServicoRealizado();
            novoServico.setVeiculoPlaca(placa);
            novoServico.setDescricao(descricao);
            novoServico.setValor(valor);
            novoServico.setFormaPagamento(formaPagamento);
            novoServico.setUsuario(usuarioLogado); // 👈 AGORA SIM O SERVIÇO CONHECE A OFICINA!

            // Tratamento da data vinda da tela
            if (payload.get("dataServico") != null && !payload.get("dataServico").toString().isEmpty()) {
                novoServico.setDataServico(java.time.LocalDate.parse(payload.get("dataServico").toString()));
            } else {
                novoServico.setDataServico(java.time.LocalDate.now());
            }

            // Grava o serviço realizado no MySQL
            ServicoRealizado servicoSalvo = servicoRealizadoRepository.save(novoServico);

            // 4. 📝 SE FOR A PRAZO (FIADO): Grava os dados do devedor na tabela isolada
            if ("A_PRAZO".equalsIgnoreCase(formaPagamento)) {
                ClienteDevedor devedor = new ClienteDevedor();
                devedor.setNome(payload.get("fiadoNome") != null ? payload.get("fiadoNome").toString() : "Não informado");
                devedor.setApelido(payload.get("fiadoApelido") != null ? payload.get("fiadoApelido").toString() : "");
                devedor.setDocumento(payload.get("fiadoDocumento") != null ? payload.get("fiadoDocumento").toString() : "");
                devedor.setTelefone(payload.get("fiadoTelefone") != null ? payload.get("fiadoTelefone").toString() : "");

                // Inicializa a régua da dívida administrativa
                devedor.setValorOriginal(valor);
                devedor.setSaldoDevedor(valor);
                devedor.setDataRegistro(novoServico.getDataServico());

                // Faz o vínculo relacional com o serviço gerador da cobrança
                devedor.setServicoRealizado(servicoSalvo);

                // 🚀 AMARRA A OFICINA LOGADA AO DEVEDOR TAMBÉM
                devedor.setUsuario(usuarioLogado);

                // Persiste no banco MySQL
                clienteDevedorRepository.save(devedor);
            }

            return ResponseEntity.ok().body("{\"mensagem\": \"Serviço gravado com sucesso no MySQL!\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("{\"erro\": \"Erro ao salvar: " + e.getMessage() + "\"}");
        }
    }

    // ==========================================================================
    // 🚗 ROTA RÁPIDA: VERIFICA SE O VEÍCULO JÁ EXISTE ANTES DE CADASTRAR (GET)
    // ==========================================================================
    @CrossOrigin(origins = "*")
    @GetMapping("/veiculo/{placa}")
    public ResponseEntity<?> buscarVeiculoPorPlaca(@PathVariable String placa) {
        try {
            String placaBusca = placa.trim().toUpperCase();
            java.util.Optional<com.example.diagnosticarapi.model.Veiculo> veiculoOpt = veiculoRepository.findById(placaBusca);

            if (veiculoOpt.isPresent()) {
                return ResponseEntity.ok(veiculoOpt.get());
            } else {
                return ResponseEntity.status(404).body("{\"mensagem\": \"Veículo não encontrado\"}");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"erro\": \"" + e.getMessage() + "\"}");
        }
    }

    // ==========================================================================
    // 🔍 ROTA PARA BUSCAR O HISTÓRICO COMPLETO DO CARRO PELA PLACA (GET)
    // ==========================================================================
    @CrossOrigin(origins = "*")
    @GetMapping("/cliente/{placa}")
    public ResponseEntity<?> buscarHistoricoCliente(@PathVariable String placa) {
        try {
            String placaBusca = placa.trim().toUpperCase();

            java.util.Optional<Veiculo> veiculoOpt = veiculoRepository.findById(placaBusca);

            if (veiculoOpt.isEmpty()) {
                return ResponseEntity.status(404).body("{\"erro\": \"Veículo não cadastrado no sistema!\"}");
            }

            Veiculo veiculo = veiculoOpt.get();
            java.util.List<ServicoRealizado> historico = servicoRealizadoRepository.findByVeiculoPlaca(placaBusca);

            java.util.Map<String, Object> resposta = new java.util.HashMap<>();
            resposta.put("modelo", veiculo.getModelo());
            resposta.put("ano", String.valueOf(veiculo.getAno()));
            resposta.put("historico", historico);

            return ResponseEntity.ok(resposta);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"erro\": \"" + e.getMessage() + "\"}");
        }
    }

    // ==========================================================================
// 📊 ROTA PARA BALANÇO FINANCEIRO POR OFICINA (GET)
// ==========================================================================
    @CrossOrigin(origins = "*")
    @GetMapping("/balanco/{usuarioId}")
    public ResponseEntity<?> obterBalancoFinanceiro(@PathVariable Long usuarioId) {
        try {
            List<ServicoRealizado> servicos = servicoRealizadoRepository.findByUsuarioId(usuarioId);

            double totalGeral = 0.0;
            double totalVista = 0.0;
            double totalFiado = 0.0;

            for (ServicoRealizado s : servicos) {
                double valor = s.getValor();
                totalGeral += valor;

                if ("A_PRAZO".equalsIgnoreCase(s.getFormaPagamento()) || "FIADO".equalsIgnoreCase(s.getFormaPagamento())) {
                    totalFiado += valor;
                } else {
                    totalVista += valor;
                }
            }

            java.util.Map<String, Object> resumo = new java.util.HashMap<>();
            resumo.put("totalGeral", totalGeral);
            resumo.put("totalVista", totalVista);
            resumo.put("totalFiado", totalFiado);
            resumo.put("quantidadeServicos", servicos.size());

            return ResponseEntity.ok(resumo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"erro\": \"" + e.getMessage() + "\"}");
        }
    }
}