package com.bank.banking_service.controllers;

import com.bank.banking_service.model.Card;
import com.bank.banking_service.services.CardService;
import com.bank.banking_service.utils.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/banking/cards")
public class CardController {

    private final CardService cardService;
    private final JwtUtil jwtUtil;

    public CardController(CardService cardService, JwtUtil jwtUtil) {
        this.cardService = cardService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Client gets his own cards
     */
    @GetMapping("/my")
    public ResponseEntity<List<Card>> getUserCards(@RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.extractUserId(token.substring(7));
        return ResponseEntity.ok(cardService.getUserCards(userId));
    }



    /**
     * Client requests a debit card
     */
    @PostMapping("/debit")
    public ResponseEntity<Card> requestDebitCard(
            @RequestHeader("Authorization") String token,
            @RequestParam Long accountId) {
        Long userId = jwtUtil.extractUserId(token.substring(7));
        return ResponseEntity.ok(cardService.requestDebitCard(userId, accountId));
    }

    /**
     * Client requests a Credit Card
     */
    @PostMapping("/credit")
    public ResponseEntity<Card> requestCreditCard(
            @RequestHeader("Authorization") String token,
            @RequestParam Double salary) {
        Long userId = jwtUtil.extractUserId(token.substring(7));
        return ResponseEntity.ok(cardService.requestCreditCard(userId, salary));
    }

    /**
     * Banker approves credit card
     */
    @PutMapping("/{cardId}/approve")
    public ResponseEntity<Card> approveCreditCard(
            @PathVariable Long cardId,
            @RequestParam Double limit) {
        return ResponseEntity.ok(cardService.approveCreditCard(cardId, limit));
    }

    /**
     * Banker get pending credit card requests
     */
    @GetMapping("/pending")
    public ResponseEntity<List<Card>> getPendingCreditCards() {
        return ResponseEntity.ok(cardService.getPendingCreditCards());
    }
}
