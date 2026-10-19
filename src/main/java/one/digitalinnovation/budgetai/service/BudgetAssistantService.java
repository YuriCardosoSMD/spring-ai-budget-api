package one.digitalinnovation.budgetai.service;

import one.digitalinnovation.budgetai.tool.TransactionTools;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.audio.speech.SpeechPrompt;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class BudgetAssistantService {

    private static final String SYSTEM_PROMPT = """
            Você é um assistente financeiro inteligente especializado em controle de orçamento pessoal.
            Sua função é entender os comandos da pessoa usuária, acionar as ferramentas adequadas para registrar
            ou consultar transações financeiras e responder sempre em português de forma clara, educada e objetiva.
            Sempre informe os valores em Reais (R$) na sua resposta final.
            """;

    private final ChatClient chatClient;
    private final TransactionTools transactionTools;
    private final OpenAiAudioTranscriptionModel transcriptionModel;
    private final OpenAiAudioSpeechModel speechModel;

    public BudgetAssistantService(
            ChatClient.Builder chatClientBuilder,
            TransactionTools transactionTools,
            OpenAiAudioTranscriptionModel transcriptionModel,
            OpenAiAudioSpeechModel speechModel
    ) {
        this.chatClient = chatClientBuilder
                .defaultSystem(SYSTEM_PROMPT)
                .build();
        this.transactionTools = transactionTools;
        this.transcriptionModel = transcriptionModel;
        this.speechModel = speechModel;
    }

    public String processTextCommand(String userMessage) {
        return chatClient.prompt()
                .user(userMessage)
                .tools(transactionTools)
                .call()
                .content();
    }

    public String transcribeAudio(Resource audioResource) {
        AudioTranscriptionPrompt prompt = new AudioTranscriptionPrompt(audioResource);
        return transcriptionModel.call(prompt).getResult().getOutput();
    }

    public String processVoiceCommand(Resource audioResource) {
        String transcribedText = transcribeAudio(audioResource);
        String aiResponse = processTextCommand(transcribedText);
        return String.format("Transcrição reconhecida: \"%s\"\n\nResposta do Assistente: %s", transcribedText, aiResponse);
    }

    public byte[] synthesizeSpeech(String text) {
        SpeechPrompt speechPrompt = new SpeechPrompt(text);
        return speechModel.call(speechPrompt).getResult().getOutput();
    }
}