package one.digitalinnovation.budgetai.repository;

import java.util.List;
import one.digitalinnovation.budgetai.model.Transaction;
import one.digitalinnovation.budgetai.model.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByCategoryIgnoreCase(String category);

    List<Transaction> findByType(TransactionType type);
}