package com.bank.banking_service.services;

import com.bank.banking_service.model.AccountStatus;
import com.bank.banking_service.model.AccountType;
import com.bank.banking_service.model.BankAccount;
import com.bank.banking_service.model.Currency;
import com.bank.banking_service.repositories.BankAccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class BankAccountService {

    private static final Logger logger = LoggerFactory.getLogger(BankAccountService.class);
    private final BankAccountRepository bankAccountRepository;

    public BankAccountService(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    /**
     * Client gets his own bank accounts
     */
    public List<BankAccount> getUserBankAccounts(Long userId) {
        logger.info("Fetching bank accounts for userId: {}", userId);
        List<BankAccount> accounts = bankAccountRepository.findByUserId(userId);
        logger.info("Retrieved {} accounts for userId: {}", accounts.size(), userId);
        return accounts;
    }

    /**
     * Banker gets all bank accounts
     */
    public List<BankAccount> getAllBankAccounts() {
        logger.info("Fetching all bank accounts");
        List<BankAccount> accounts = bankAccountRepository.findAll();
        logger.info("Retrieved {} total accounts", accounts.size());
        return accounts;
    }

    /**
     * Client requests a new current account
     */
    public BankAccount requestCurrentAccount(Long userId, String role) {
        logger.info("User {} is requesting a new Current Account with role {}", userId, role);

        if (!role.equalsIgnoreCase("CLIENT")) {
            logger.warn("Unauthorized account request by userId: {} with role: {}", userId, role);
            throw new RuntimeException("Only clients can request accounts.");
        }

        // Create a new Current Account (unapproved)
        BankAccount account = new BankAccount();
        account.setIban(UUID.randomUUID().toString());
        account.setUserId(userId);
        account.setType(AccountType.CURRENT);
        account.setStatus(AccountStatus.PENDING);
        account.setBalance(0.0);
        account.setCurrency(Currency.EUR);
        account.setInterest(0.0);

        BankAccount savedAccount = bankAccountRepository.save(account);
        logger.info("New Current Account requested by userId: {} | IBAN: {} | Status: PENDING", userId, savedAccount.getIban());

        return savedAccount;
    }

    /**
     * Approve or disapprove a bank account
     */
    public BankAccount approveOrDisapproveAccount(Long accountId, String accountStatus) {
        logger.info("Processing account approval/disapproval for accountId: {} with status: {}", accountId, accountStatus);

        BankAccount account = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> {
                    logger.error("Account not found for accountId: {}", accountId);
                    return new RuntimeException("Account not found");
                });

        if (account.getStatus().equals(AccountStatus.APPROVED)) {
            logger.warn("Attempt to re-approve already approved accountId: {}", accountId);
            throw new RuntimeException("Account is already approved.");
        }

        account.setStatus(AccountStatus.valueOf(accountStatus));
        BankAccount updatedAccount = bankAccountRepository.save(account);
        logger.info("Account status updated: accountId: {} | New Status: {}", accountId, accountStatus);

        return updatedAccount;
    }

    /**
     * Get all unapproved accounts (for Banker to review)
     */
    public List<BankAccount> getPendingAccounts() {
        logger.info("Fetching all pending bank account requests");
        List<BankAccount> pendingAccounts = bankAccountRepository.findByStatus(AccountStatus.PENDING);
        logger.info("Retrieved {} pending accounts", pendingAccounts.size());
        return pendingAccounts;
    }
}