package com.bank.banking_service.repositories;

import com.bank.banking_service.dtos.CreditDetails;
import com.bank.banking_service.model.AccountStatus;
import com.bank.banking_service.model.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {


    List<BankAccount> findByStatus(AccountStatus accountStatus);

    Optional<BankAccount> findByIban(String iban);

    @Query("SELECT COUNT(c) FROM Card c WHERE c.bankAccount.id = :accountId")
    int countLinkedCards(@Param("accountId") Long accountId);

    @Query("SELECT c.creditLimit AS creditLimit, c.interestRate AS interestRate " +
            "FROM Card c WHERE c.bankAccount.id = :accountId")
    Optional<CreditDetails> getCreditDetails(@Param("accountId") Long accountId);

    List<BankAccount> findByUserId(Long userId);
}
