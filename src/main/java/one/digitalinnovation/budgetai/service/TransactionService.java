package one.digitalinnovation.budgetai.service;

import java.math.BigDecimal;
import java.util.List;
import one.digitalinnovation.budgetai.model.FinancialSummaryDTO;
import one.digitalinnovation.budgetai.model.Transaction;
import one.digitalinnovation.budgetai.model.TransactionType;
import one.digitalinnovation.budgetai.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public Transaction createTransaction(Transaction transaction) {
        if (transaction.getAmount() == null || transaction.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor da transação deve ser positivo e maior que zero.");
        }
        if (transaction.getDescription() == null || transaction.getDescription().isBlank()) {
            throw new IllegalArgumentException("A descrição da transação não pode estar vazia.");
        }
        if (transaction.getCategory() == null || transaction.getCategory().isBlank()) {
            transaction.setCategory("Geral");
        }
        return transactionRepository.save(transaction);
    }

    public List<Transaction> listAll() {
        return transactionRepository.findAll();
    }

    public List<Transaction> listByCategory(String category) {
        return transactionRepository.findByCategoryIgnoreCase(category);
    }

    public List<Transaction> listByType(TransactionType type) {
        return transactionRepository.findByType(type);
    }

    @Transactional
    public void deleteById(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new IllegalArgumentException("Transação não encontrada para o ID: " + id);
        }
        transactionRepository.deleteById(id);
    }

    public FinancialSummaryDTO getSummary() {
        List<Transaction> all = transactionRepository.findAll();

        BigDecimal totalIncome = all.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpense = all.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal balance = totalIncome.subtract(totalExpense);

        return new FinancialSummaryDTO(totalIncome, totalExpense, balance, all.size());
    }
}