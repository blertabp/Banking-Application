package com.bank.banking_service.repositories;

import com.bank.banking_service.model.Card;
import com.bank.banking_service.model.CardStatus;
import com.bank.banking_service.model.CardType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    boolean existsByUserIdAndType(Long userId, CardType type);


    boolean existsByBankAccountId(Long accountId);

    List<Card> findByStatusAndType(CardStatus cardStatus, CardType cardType);

    List<Card> findByUserId(Long userId);
}
