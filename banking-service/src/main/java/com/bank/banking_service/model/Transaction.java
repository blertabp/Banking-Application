package com.bank.banking_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long bankAccountId;

    @Column(nullable = false)
    private String iban;

    @Column(nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency currency=Currency.EUR;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();


    public Transaction(Long bankAccountId, String iban, Double amount, Currency currency, TransactionType type, LocalDateTime timestamp) {
        this.bankAccountId = bankAccountId;
        this.iban = iban;
        this.amount = amount;
        this.currency = currency;
        this.type = type;
        this.timestamp = timestamp;
    }

}

