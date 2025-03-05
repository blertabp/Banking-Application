package com.bank.banking_service.services;

import com.bank.banking_service.model.*;
import com.bank.banking_service.repositories.BankAccountRepository;
import com.bank.banking_service.repositories.CardRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class CardService {

    private static final Logger logger = LoggerFactory.getLogger(CardService.class);

    private final CardRepository cardRepository;
    private final BankAccountRepository bankAccountRepository;

    public CardService(CardRepository cardRepository, BankAccountRepository bankAccountRepository) {
        this.cardRepository = cardRepository;
        this.bankAccountRepository = bankAccountRepository;
    }

    /**
     * Retrieves all cards associated with the given user.
     */
    public List<Card> getUserCards(Long userId) {
        logger.info("Fetching cards for userId: {}", userId);
        List<Card> cards = cardRepository.findByUserId(userId);
        logger.info("Retrieved {} cards for userId: {}", cards.size(), userId);
        return cards;
    }

    /**
     * Client Requests a Debit Card (Must Link to an Approved Current Account)
     */
    public Card requestDebitCard(Long userId, Long accountId) {
        logger.info("User {} is requesting a Debit Card for account {}", userId, accountId);

        BankAccount account = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> {
                    logger.error("Bank Account {} not found", accountId);
                    return new RuntimeException("Account not found");
                });

        if (!account.getStatus().equals(AccountStatus.APPROVED)) {
            logger.warn("Attempt to link Debit Card to an unapproved account {}", accountId);
            throw new RuntimeException("Account is not approved.");
        }

        if (account.getType() != AccountType.CURRENT) {
            logger.warn("Attempt to link Debit Card to a non-current account {}", accountId);
            throw new RuntimeException("Debit Card can only be linked to a Current Account.");
        }

        if (cardRepository.existsByBankAccountId(accountId)) {
            logger.warn("Attempt to link another card to account {}", accountId);
            throw new RuntimeException("This account already has a linked card.");
        }

        Card card = new Card();
        card.setType(CardType.DEBIT);
        card.setUserId(userId);
        card.setBankAccount(account);
        card.setStatus(CardStatus.APPROVED); // Debit Cards are auto-approved

        Card savedCard = cardRepository.save(card);
        logger.info("Debit Card successfully created for userId: {} | Account: {}", userId, accountId);

        return savedCard;
    }

    /**
     * Client Requests a Credit Card (Salary-Based Approval)
     */
    public Card requestCreditCard(Long userId, Double salary) {
        logger.info("User {} is requesting a Credit Card with salary {}", userId, salary);

        if (salary < 500) {
            logger.warn("Credit Card request denied for user {} due to low salary: {}", userId, salary);
            throw new RuntimeException("Salary too low for a Credit Card.");
        }

        if (cardRepository.existsByUserIdAndType(userId, CardType.CREDIT)) {
            logger.warn("User {} already has a Credit Card", userId);
            throw new RuntimeException("User already has a Credit Card.");
        }

        Card card = new Card();
        card.setType(CardType.CREDIT);
        card.setUserId(userId);
        card.setInterestRate(salary <= 1000 ? 10.0 : 8.0);
        card.setStatus(CardStatus.PENDING); // Needs Banker approval

        Card savedCard = cardRepository.save(card);
        logger.info("Credit Card request created for userId: {} | Status: PENDING", userId);

        return savedCard;
    }

    /**
     * Banker Approves the Credit Card (Sets Credit Limit and Creates Technical Account)
     */
    public Card approveCreditCard(Long cardId, Double limit) {
        logger.info("Approving Credit Card ID {} with limit {}", cardId, limit);

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> {
                    logger.error("Credit Card {} not found", cardId);
                    return new RuntimeException("Card not found");
                });

        if (card.getType() != CardType.CREDIT) {
            logger.warn("Attempt to approve a non-credit card {}", cardId);
            throw new RuntimeException("Only Credit Cards require approval.");
        }

        if (card.getStatus() == CardStatus.APPROVED) {
            logger.warn("Credit Card {} is already approved", cardId);
            throw new RuntimeException("Credit Card is already approved.");
        }

        // Create a linked Technical Account
        BankAccount technicalAccount = new BankAccount();
        technicalAccount.setIban(UUID.randomUUID().toString());
        technicalAccount.setUserId(card.getUserId());
        technicalAccount.setType(AccountType.TECHNICAL);
        technicalAccount.setStatus(AccountStatus.APPROVED);
        technicalAccount.setBalance(0.0);
        technicalAccount = bankAccountRepository.save(technicalAccount);

        // Link the Technical Account to the card
        card.setBankAccount(technicalAccount);
        card.setCreditLimit(limit);
        card.setStatus(CardStatus.APPROVED);

        Card updatedCard = cardRepository.save(card);
        logger.info("Credit Card {} approved with limit {} | Linked to Technical Account {}", cardId, limit, technicalAccount.getIban());

        return updatedCard;
    }

    /**
     * Get all pending credit card requests
     */
    public List<Card> getPendingCreditCards() {
        logger.info("Fetching all pending Credit Card requests");
        List<Card> pendingCards = cardRepository.findByStatusAndType(CardStatus.PENDING, CardType.CREDIT);
        logger.info("Retrieved {} pending Credit Card requests", pendingCards.size());
        return pendingCards;
    }
}
