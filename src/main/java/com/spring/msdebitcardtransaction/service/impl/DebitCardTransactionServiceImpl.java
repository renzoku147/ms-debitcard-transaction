package com.spring.msdebitcardtransaction.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.deser.impl.ExternalTypeHandler.Builder;
import com.spring.msdebitcardtransaction.entity.Credit;
import com.spring.msdebitcardtransaction.entity.CreditTransaction;
import com.spring.msdebitcardtransaction.entity.DebitCard;
import com.spring.msdebitcardtransaction.entity.DebitCardTransaction;
import com.spring.msdebitcardtransaction.repository.DebitCardTransactionRepository;
import com.spring.msdebitcardtransaction.service.DebitCardTransactionService;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class DebitCardTransactionServiceImpl implements DebitCardTransactionService{

    WebClient webClientCredit = WebClient.create("http://localhost:8887/ms-credit-charge/creditCharge");
    
    WebClient webClientCreditPaid = WebClient.create("http://localhost:8887/ms-credit-pay/creditPaid");
    
    WebClient webClientDebitCard = WebClient.create("http://localhost:8887/ms-debitcard/debitCard");
	
    @Autowired
    private DebitCardTransactionRepository debitCardTransactionRepository;
    
	@Override
	public Mono<DebitCardTransaction> create(DebitCardTransaction t) {
		return debitCardTransactionRepository.save(t);
	}

	@Override
	public Flux<DebitCardTransaction> findAll() {
		return debitCardTransactionRepository.findAll();
	}

	@Override
    public Mono<DebitCardTransaction> findById(String id) {
        return debitCardTransactionRepository.findById(id);
    }

    @Override
    public Mono<DebitCardTransaction> update(DebitCardTransaction t) {
        return debitCardTransactionRepository.save(t);
    }

    @Override
    public Mono<Boolean> delete(String t) {
        return debitCardTransactionRepository.findById(t)
                .flatMap(tar -> debitCardTransactionRepository.delete(tar).then(Mono.just(Boolean.TRUE)))
                .defaultIfEmpty(Boolean.FALSE);
    }

    @Override
    public Flux<DebitCardTransaction> findCreditsPaid(String id) {
    	
        return Flux.merge(debitCardTransactionRepository.findByCreditId(id),
        					webClientCreditPaid.get().uri("/findAmountCreditsPaidDebitCard/{id}", id)
			                .accept(MediaType.APPLICATION_JSON)
			                .retrieve()
			                .bodyToFlux(CreditTransaction.class)
			                .map(dc -> DebitCardTransaction.builder().transactionAmount(dc.getTransactionAmount()).build()));
    }

    @Override
    public Mono<Credit> findCredit(String id) {
        return webClientCredit.get().uri("/find/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Credit.class);
    }

	@Override
	public Mono<DebitCardTransaction> checkUpdateBalanceDebitCard(String cardNumber, DebitCardTransaction credit) {
		log.info("");
		return webClientDebitCard.put().uri("/checkUpdateBalanceDebitCard/{cardNumber}", cardNumber)
                .accept(MediaType.APPLICATION_JSON)
                .syncBody(credit)
                .retrieve()
                .bodyToMono(DebitCardTransaction.class);
	}

	@Override
	public Mono<DebitCard> findDebitCard(String cardNumber) {
		return webClientDebitCard.get().uri("/findCreditCardByCardNumber/{cardNumber}", cardNumber)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(DebitCard.class);
	}

	@Override
	public Flux<DebitCardTransaction> findAmountCreditsPaidDebitCard(String id) {
		return debitCardTransactionRepository.findByCreditId(id);
	}

	@Override
	public Flux<DebitCardTransaction> findByCreditCreditCardCustomerId(String idcustomer) {
		return debitCardTransactionRepository.findByCreditCreditCardCustomerId(idcustomer);
	}
	
	

}
