package one.digitalinnovation.budgetai.tool;

import java.math.BigDecimal;
import java.util.List;
import one.digitalinnovation.budgetai.model.FinancialSummaryDTO;
import one.digitalinnovation.budgetai.model.Transaction;
import one.digitalinnovation.budgetai.model.TransactionType;
import one.digitalinnovation.budgetai.service.TransactionService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
public class TransactionTools {

    private final TransactionService transactionService;

    public TransactionTools(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Tool(description = "Registra uma nova transação financeira (receita ou despesa) no banco de dados")
    public Transaction registrarTransacao(
            @ToolParam(description = "Descrição curta da transação, ex: Supermercado, Salário, Uber") String description,
            @ToolParam(description = "Valor numérico positivo da transação, ex: 150.50") BigDecimal amount,
            @ToolParam(description = "Categoria da transação, ex: Alimentação, Transporte, Renda, Moradia") String category,
            @ToolParam(description = "Tipo da transação: INCOME para receita/entrada ou EXPENSE para despesa/gasto") TransactionType type
    ) {
        Transaction nova = new Transaction(description, amount, category, type);
        return transactionService.createTransaction(nova);
    }

    @Tool(description = "Lista todas as transações financeiras cadastradas no orçamento")
    public List<Transaction> listarTodasTransacoes() {
        return transactionService.listAll();
    }

    @Tool(description = "Consulta transações financeiras filtrando por uma categoria específica, como Alimentação, Lazer ou Transporte")
    public List<Transaction> consultarPorCategoria(
            @ToolParam(description = "Nome da categoria a ser buscada") String category
    ) {
        return transactionService.listByCategory(category);
    }

    @Tool(description = "Calcula e retorna o resumo financeiro completo com total de receitas, total de despesas e saldo disponível")
    public FinancialSummaryDTO consultarResumoFinanceiro() {
        return transactionService.getSummary();
    }
}