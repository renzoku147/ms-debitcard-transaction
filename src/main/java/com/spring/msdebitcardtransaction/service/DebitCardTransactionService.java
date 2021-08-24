package com.spring.msdebitcardtransaction.service;


import com.spring.msdebitcardtransaction.entity.Credit;
import com.spring.msdebitcardtransaction.entity.DebitCard;
import com.spring.msdebitcardtransaction.entity.DebitCardTransaction;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DebitCardTransactionService {
    Mono<DebitCardTransaction> create(DebitCardTransaction t);

    Flux<DebitCardTransaction> findAll();

    Mono<DebitCardTransaction> findById(String id);

    Mono<DebitCardTransaction> update(DebitCardTransaction t);

    Mono<Boolean> delete(String t);

    Flux<DebitCardTransaction> findCreditsPaid(String id);

    Mono<Credit> findCredit(String id);
    
    Mono<DebitCardTransaction> checkUpdateBalanceDebitCard(String cardNumber, DebitCardTransaction credit);
    
    Mono<DebitCard> findDebitCard(String numberAccount);
    
    Flux<DebitCardTransaction> findAmountCreditsPaidDebitCard(String id);
    
    Flux<DebitCardTransaction> findByCreditCreditCardCustomerId(String id);
}
