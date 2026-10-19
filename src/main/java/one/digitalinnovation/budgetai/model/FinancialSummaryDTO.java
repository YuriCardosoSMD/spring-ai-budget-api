package one.digitalinnovation.budgetai.model;

import java.math.BigDecimal;

public record FinancialSummaryDTO(
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal balance,
        long transactionCount
) {
}