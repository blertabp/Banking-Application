package com.bank.banking_service.repositories;

import com.bank.banking_service.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {


    @Query("SELECT t FROM Transaction t WHERE t.bankAccountId IN " +
            "(SELECT b.id FROM BankAccount b WHERE b.userId = :userId)")
    List<Transaction> findByUserId(@Param("userId") Long userId);

}

