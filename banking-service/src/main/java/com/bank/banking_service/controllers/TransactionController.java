package com.bank.banking_service.controllers;

import com.bank.banking_service.model.Transaction;
import com.bank.banking_service.services.TransactionService;
import com.bank.banking_service.utils.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/banking/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final JwtUtil jwtUtil;

    public TransactionController(TransactionService transactionService, JwtUtil jwtUtil) {
        this.transactionService = transactionService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Banker gets all transactions
     */
    @GetMapping("/all")
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    /**
     * Client gets his own transactions
     */
    @GetMapping("/my")
    public ResponseEntity<List<Transaction>> getUserTransactions(@RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.extractUserId(token.substring(7));
        return ResponseEntity.ok(transactionService.getUserTransactions(userId));
    }

    /**
     * Client performs transaction
     */
    @PostMapping("/transfer")
    public ResponseEntity<Transaction> transferMoney(
            @RequestParam Long senderAccountId,
            @RequestParam String receiverIban,
            @RequestParam Double amount) {

        Transaction transaction = transactionService.executeTransaction(senderAccountId, receiverIban, amount);
        return ResponseEntity.ok(transaction);
    }
}
