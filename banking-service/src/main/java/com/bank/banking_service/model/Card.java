package com.bank.banking_service.model;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CardType type;

    @Column(nullable = false)
    private Long userId;

    @OneToOne
    @JoinColumn(name = "bank_account_id", unique = true,referencedColumnName = "id")
    private BankAccount bankAccount;

    private Double creditLimit;

    private Double interestRate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CardStatus status;

}
