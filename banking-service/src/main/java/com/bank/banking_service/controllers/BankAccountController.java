package com.bank.banking_service.controllers;

import com.bank.banking_service.model.BankAccount;
import com.bank.banking_service.services.BankAccountService;
import com.bank.banking_service.utils.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/banking/accounts")
public class BankAccountController {

    private final BankAccountService bankAccountService;
    private final JwtUtil jwtUtil;

    public BankAccountController(BankAccountService bankAccountService, JwtUtil jwtUtil) {
        this.bankAccountService = bankAccountService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Client gets his own bank accounts
     */
    @GetMapping("/my")
    public ResponseEntity<List<BankAccount>> getUserBankAccounts(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(bankAccountService.getUserBankAccounts(jwtUtil.extractUserId(token.substring(7))));
    }

    /**
     * Banker gets all bank accounts
     */
    @GetMapping("/all")
    public ResponseEntity<List<BankAccount>> getAllBankAccounts() {
        return ResponseEntity.ok(bankAccountService.getAllBankAccounts());
    }

    /**
     * Client requests a current account
     */
    @PostMapping("/request")
    public ResponseEntity<BankAccount> requestCurrentAccount(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(bankAccountService.requestCurrentAccount(jwtUtil.extractUserId(token.substring(7)), jwtUtil.extractRole(token.substring(7))));
    }

    /**
     * Banker approves or disapprove account requests
     */
    @PutMapping("/{accountId}/approval")
    public ResponseEntity<BankAccount> approveOrDisapproveAccount(
            @PathVariable Long accountId,
            @RequestParam String accountStatus) {
        return ResponseEntity.ok(bankAccountService.approveOrDisapproveAccount(accountId, accountStatus));
    }

    /**
     * Banker get all pending account requests
     */
    @GetMapping("/pending")
    public ResponseEntity<List<BankAccount>> getPendingAccounts() {
        return ResponseEntity.ok(bankAccountService.getPendingAccounts());
    }
}
