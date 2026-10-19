package one.digitalinnovation.budgetai.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import one.digitalinnovation.budgetai.model.FinancialSummaryDTO;
import one.digitalinnovation.budgetai.model.Transaction;
import one.digitalinnovation.budgetai.service.BudgetAssistantService;
import one.digitalinnovation.budgetai.service.TransactionService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/transactions")
@Tag(name = "Orçamento Inteligente (Spring AI)", description = "Endpoints REST e Comandos de Voz/Texto com IA")
public class TransactionController {

    private final TransactionService transactionService;
    private final BudgetAssistantService assistantService;

    public TransactionController(TransactionService transactionService, BudgetAssistantService assistantService) {
        this.transactionService = transactionService;
        this.assistantService = assistantService;
    }

    @GetMapping
    @Operation(summary = "Lista todas as transações financeiras")
    public ResponseEntity<List<Transaction>> listAll(
            @RequestParam(required = false) String category
    ) {
        if (category != null && !category.isBlank()) {
            return ResponseEntity.ok(transactionService.listByCategory(category));
        }
        return ResponseEntity.ok(transactionService.listAll());
    }

    @GetMapping("/summary")
    @Operation(summary = "Consulta o resumo financeiro (total de receitas, despesas e saldo)")
    public ResponseEntity<FinancialSummaryDTO> getSummary() {
        return ResponseEntity.ok(transactionService.getSummary());
    }

    @PostMapping
    @Operation(summary = "Cadastra manualmente uma nova transação financeira validada")
    public ResponseEntity<Transaction> create(@Valid @RequestBody Transaction transaction) {
        return ResponseEntity.ok(transactionService.createTransaction(transaction));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove uma transação financeira pelo ID")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        transactionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/ai/command")
    @Operation(summary = "Processa um comando em linguagem natural (texto) usando IA e Tool Calling")
    public ResponseEntity<Map<String, String>> processTextCommand(@RequestBody Map<String, String> payload) {
        String command = payload.getOrDefault("command", "");
        String response = assistantService.processTextCommand(command);
        return ResponseEntity.ok(Map.of("command", command, "response", response));
    }

    @PostMapping(value = "/ai/voice", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Recebe um áudio, transcreve em texto, executa a função via IA e retorna a resposta escrita")
    public ResponseEntity<Map<String, String>> processVoiceCommand(@RequestParam("audio") MultipartFile audioFile) {
        String result = assistantService.processVoiceCommand(audioFile.getResource());
        return ResponseEntity.ok(Map.of("result", result));
    }

    @PostMapping(value = "/ai/voice-to-speech", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "audio/mpeg")
    @Operation(summary = "Fluxo completo: recebe comando em áudio, executa no banco via IA e devolve resposta em voz (MP3)")
    public ResponseEntity<byte[]> processVoiceAndReplyWithSpeech(@RequestParam("audio") MultipartFile audioFile) {
        String transcribed = assistantService.transcribeAudio(audioFile.getResource());
        String aiResponse = assistantService.processTextCommand(transcribed);
        byte[] speechAudio = assistantService.synthesizeSpeech(aiResponse);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"resposta-orcamento.mp3\"")
                .contentType(MediaType.valueOf("audio/mpeg"))
                .body(speechAudio);
    }
}