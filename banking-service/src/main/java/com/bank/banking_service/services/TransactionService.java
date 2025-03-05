package com.bank.banking_service.services;

import com.bank.banking_service.dtos.CreditDetails;
import com.bank.banking_service.model.AccountType;
import com.bank.banking_service.model.BankAccount;
import com.bank.banking_service.model.Transaction;
import com.bank.banking_service.model.TransactionType;
import com.bank.banking_service.repositories.BankAccountRepository;
import com.bank.banking_service.repositories.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private final BankAccountRepository bankAccountRepository;
    private final TransactionRepository transactionRepository;

    public TransactionService(BankAccountRepository bankAccountRepository, TransactionRepository transactionRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.transactionRepository = transactionRepository;
    }

    public List<Transaction> getUserTransactions(Long userId) {
        logger.info("Fetching transactions for userId: {}", userId);
        List<Transaction> transactions = transactionRepository.findByUserId(userId);
        logger.info("Retrieved {} transactions for userId: {}", transactions.size(), userId);
        return transactions;
    }

    public List<Transaction> getAllTransactions() {
        logger.info("Fetching all transactions.");
        return transactionRepository.findAll(); // Bankers can view all transactions
    }

    /**
     * Perform a Transaction
     */
    @Transactional
    public Transaction executeTransaction(Long senderAccountId, String receiverIban, Double amount) {
        logger.info("Initiating transaction from senderAccountId {} to IBAN {} with amount {}", senderAccountId, receiverIban, amount);

        if (amount <= 0) {
            logger.error("Transaction failed: Invalid amount {}", amount);
            throw new RuntimeException("Invalid transaction amount.");
        }

        // Fetch sender and receiver accounts
        BankAccount senderAccount = bankAccountRepository.findById(senderAccountId)
                .orElseThrow(() -> {
                    logger.error("Transaction failed: Sender account {} not found", senderAccountId);
                    return new RuntimeException("Sender account not found");
                });

        BankAccount receiverAccount = bankAccountRepository.findByIban(receiverIban)
                .orElseThrow(() -> {
                    logger.error("Transaction failed: Receiver account with IBAN {} not found", receiverIban);
                    return new RuntimeException("Receiver account not found");
                });

        // Check if sender has a linked card
        if (!hasLinkedCard(senderAccountId)) {
            logger.warn("Transaction failed: Sender account {} does not have a linked card", senderAccountId);
            throw new RuntimeException("Sender account must have a linked card.");
        }

        // Validate transaction based on account type
        if (senderAccount.getType() == AccountType.CURRENT) {
            if (senderAccount.getBalance() < amount) {
                logger.warn("Transaction failed: Insufficient balance in sender account {}", senderAccountId);
                throw new RuntimeException("Insufficient balance.");
            }
        } else if (senderAccount.getType() == AccountType.TECHNICAL) {
            CreditDetails creditDetails = getCreditDetails(senderAccountId);
            double maxAllowedBalance = senderAccount.getBalance() - amount;

            if (maxAllowedBalance < -creditDetails.getCreditLimit()) {
                logger.warn("Transaction failed: Credit limit exceeded for account {}", senderAccountId);
                throw new RuntimeException("Credit limit exceeded.");
            }

            // Apply interest
            double interestRate = creditDetails.getInterestRate();
            amount += (amount * interestRate / 100);
            logger.info("Interest applied: New transaction amount is {}", amount);
        }

        // Update sender and receiver balances
        senderAccount.setBalance(senderAccount.getBalance() - amount);
        receiverAccount.setBalance(receiverAccount.getBalance() + amount);

        bankAccountRepository.save(senderAccount);
        bankAccountRepository.save(receiverAccount);

        // Create transaction records for both sender and receiver
        Transaction senderTransaction = new Transaction(senderAccountId, receiverIban, amount, senderAccount.getCurrency(), TransactionType.DEBIT, LocalDateTime.now());
        Transaction receiverTransaction = new Transaction(receiverAccount.getId(), senderAccount.getIban(), amount, receiverAccount.getCurrency(), TransactionType.CREDIT, LocalDateTime.now());

        transactionRepository.save(senderTransaction);
        transactionRepository.save(receiverTransaction);

        logger.info("Transaction successful! Sender Account: {} | Receiver IBAN: {} | Amount: {}", senderAccountId, receiverIban, amount);
        return senderTransaction;
    }

    /**
     * Check if account has a linked card
     */
    private boolean hasLinkedCard(Long accountId) {
        boolean linkedCardExists = bankAccountRepository.countLinkedCards(accountId) > 0;
        logger.info("Checking if account {} has a linked card: {}", accountId, linkedCardExists);
        return linkedCardExists;
    }

    private CreditDetails getCreditDetails(Long accountId) {
        logger.info("Fetching credit details for account {}", accountId);
        return bankAccountRepository.getCreditDetails(accountId).orElse(new CreditDetails() {
            @Override
            public Double getCreditLimit() {
                return 0.0;
            }

            @Override
            public Double getInterestRate() {
                return 0.0;
            }
        });
    }
}

