package com.example.diagnosticarapi.service;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.File;
import java.time.Duration;
import java.util.List;

@Service // Avisa ao Spring que essa classe cuida de uma regra de negócio externa (Serviço)
public class OpenAiServiceCustom {

    // Esse comando puxa automaticamente a chave "sk-..." que guardamos no application.properties
    @Value("${openai.api.key}")
    private String apiKey;

    /**
     /**
     * FUNÇÃO 1: Envia o arquivo de áudio recebido e pede para o Whisper transcrever em texto.
     */
    public String transcreverAudio(File arquivoAudio) {
        // Conexão com a OpenAI com tempo limite de 60 segundos
        OpenAiService service = new OpenAiService(apiKey, Duration.ofSeconds(60));

        // Criamos o pedido usando a estrutura correta que a biblioteca exige
        com.theokanning.openai.audio.CreateTranscriptionRequest request =
                com.theokanning.openai.audio.CreateTranscriptionRequest.builder()
                        .model("whisper-1")
                        .build();

        // Enviamos o modelo configurado e o arquivo físico de áudio
        return service.createTranscription(request, arquivoAudio).getText();
    }

    /**
     * FUNÇÃO 2: Pega o texto do áudio e pede para o GPT estruturar no formato de Ordem de Serviço.
     */
    public String analisarTexto(String textoTranscrito) {
        OpenAiService service = new OpenAiService(apiKey, Duration.ofSeconds(60));

        // Aqui criamos o "Prompt de Comando" dizendo exatamente como o GPT deve agir
        String instrucaoSistema = "Você é um mecânico sênior especialista em diagnóstico automotivo. " +
                "Sua tarefa é ler o relato do cliente/mecânico e estruturar um relatório técnico curto e preciso. " +
                "Use rigorosamente o seguinte formato:\n" +
                "SISTEMA AFETADO: [Nome do Sistema]\n" +
                "DIAGNÓSTICO PROVÁVEL: [O que pode estar quebrado]\n" +
                "RECOMENDAÇÃO: [O que o mecânico deve fazer]";

        // Montamos as mensagens que vão para o chat da OpenAI
        List<ChatMessage> mensagens = List.of(
                new ChatMessage(ChatMessageRole.SYSTEM.value(), instrucaoSistema),
                new ChatMessage(ChatMessageRole.USER.value(), textoTranscrito)
        );

        // Configuramos o pedido usando o modelo GPT-4o mini (super rápido e econômico!)
        ChatCompletionRequest pedidoChat = ChatCompletionRequest.builder()
                .model("gpt-4o-mini")
                .messages(mensagens)
                .temperature(0.3) // Mantém a resposta mais técnica e menos "criativa/inventada"
                .build();

        // Dispara o pedido e pega o texto da resposta da IA
        return service.createChatCompletion(pedidoChat).getChoices().get(0).getMessage().getContent();
    }
}
