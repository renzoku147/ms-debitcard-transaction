package com.spring.msdebitcardtransaction.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.spring.msdebitcardtransaction.entity.DebitCardTransaction;

import reactor.core.publisher.Flux;


public interface DebitCardTransactionRepository extends ReactiveMongoRepository<DebitCardTransaction, String> {

	Flux<DebitCardTransaction> findByCreditId(String id);
	
	Flux<DebitCardTransaction> findByCreditCreditCardCustomerId(String idcustomer);
}
