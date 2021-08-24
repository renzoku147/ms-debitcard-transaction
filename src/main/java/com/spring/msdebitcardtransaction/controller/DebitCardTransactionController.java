package com.spring.msdebitcardtransaction.controller;

import java.time.LocalDateTime;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring.msdebitcardtransaction.entity.DebitCardTransaction;
import com.spring.msdebitcardtransaction.entity.TypeTransactionDebitCard;
import com.spring.msdebitcardtransaction.service.DebitCardTransactionService;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RefreshScope
@RestController
@RequestMapping("/debitCardTransaction")
@Slf4j
public class DebitCardTransactionController {

    @Autowired
    DebitCardTransactionService debitCardTransactionService;

    @GetMapping("list")
    public Flux<DebitCardTransaction> findAll(){
        return debitCardTransactionService.findAll();
    }

    @GetMapping("/find/{id}")
    public Mono<DebitCardTransaction> findById(@PathVariable String id){
        return debitCardTransactionService.findById(id);
    }

    @PostMapping("/createPaid")
    public Mono<ResponseEntity<DebitCardTransaction>> createPaid(@Valid @RequestBody DebitCardTransaction debitCardTransaction){
        // BUSCO EL CREDITO QUE SE PRETENDE HACER EL PAGO
    	
    	return debitCardTransactionService.findCredit(debitCardTransaction.getCredit().getId())
    			.filter(credit -> debitCardTransaction.getTypeTransactionDebitCard().equals(TypeTransactionDebitCard.PAID))
    			.flatMap(credit -> debitCardTransactionService.findDebitCard(debitCardTransaction.getDebitCard().getCardNumber())
    					.flatMap(debitCard -> debitCardTransactionService.findCreditsPaid(credit.getId()) // TODAS PAGOS DE ESTE CREDITO
	                            .collectList()
	                            .filter(listCt -> credit.getAmount() >= listCt.stream().mapToDouble(ct -> ct.getTransactionAmount()).sum() + debitCardTransaction.getTransactionAmount())
	                            .flatMap(listCt -> {
	                            	log.info("Apunto de llamar el metodo para actualizar montos de las cuentas bancarias");
	                            	return debitCardTransactionService.checkUpdateBalanceDebitCard(debitCardTransaction.getDebitCard().getCardNumber(), debitCardTransaction)
                            			.filter(creditTransactionUpdate -> {
                            				log.info("Saldo DebitCard >>> " + creditTransactionUpdate.getTransactionAmount());
                            				return creditTransactionUpdate.getTransactionAmount() == 0;
                            			})
	                            		.flatMap(creditTransactionUpdate -> {
	                            			log.info("return creditTransactionUpdate Amount >>> " + creditTransactionUpdate.getTransactionAmount());
	                            			debitCardTransaction.setCredit(credit);
	                            			debitCardTransaction.setDebitCard(debitCard);
	                                        debitCardTransaction.setTransactionDateTime(LocalDateTime.now());
	                                        return debitCardTransactionService.create(debitCardTransaction);
	                            		});
	                                
	                            })
						))
				        .map(ct -> new ResponseEntity<>(ct , HttpStatus.CREATED))
				        .defaultIfEmpty(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    		
    }
    
    @PostMapping("/createRetire")
    public Mono<ResponseEntity<DebitCardTransaction>> createRetire(@Valid @RequestBody DebitCardTransaction debitCardTransaction){
    	log.info("Ejecutando retiro de dinero con tarjeta de debito");
    	return debitCardTransactionService.findDebitCard(debitCardTransaction.getDebitCard().getCardNumber())
    		.filter(credit -> {
    			log.info("Filtro 1 : " + debitCardTransaction.getTypeTransactionDebitCard() +" > " + debitCardTransaction.getTypeTransactionDebitCard().equals(TypeTransactionDebitCard.RETIRE));
    			return debitCardTransaction.getTypeTransactionDebitCard().equals(TypeTransactionDebitCard.RETIRE);
			})
    		.flatMap(debitCard -> debitCardTransactionService.checkUpdateBalanceDebitCard(debitCardTransaction.getDebitCard().getCardNumber(), debitCardTransaction)
			    				.flatMap(creditTransactionUpdate -> {
			            			log.info("return creditTransactionUpdate Amount >>> " + creditTransactionUpdate.getTransactionAmount());
			            			debitCardTransaction.setDebitCard(debitCard);
			                        debitCardTransaction.setTransactionDateTime(LocalDateTime.now());
			                        return debitCardTransactionService.create(debitCardTransaction);
			            		}))
					    		.map(ct -> new ResponseEntity<>(ct , HttpStatus.CREATED))
						        .defaultIfEmpty(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    		
    }

    @PutMapping("/update")
    public Mono<ResponseEntity<DebitCardTransaction>> update(@RequestBody DebitCardTransaction debitCardTransaction) {
        return debitCardTransactionService.findById(debitCardTransaction.getId())
                .flatMap(ctDB -> debitCardTransactionService.update(debitCardTransaction))
                .map(ct -> new ResponseEntity<>(ct , HttpStatus.CREATED))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @DeleteMapping("/delete/{id}")
    public Mono<ResponseEntity<String>> delete(@PathVariable String id) {
        return debitCardTransactionService.delete(id)
                .filter(deleteCustomer -> deleteCustomer)
                .map(deleteCustomer -> new ResponseEntity<>("Customer Deleted", HttpStatus.ACCEPTED))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @GetMapping("/findAmountCreditsPaidDebitCard/{id}")
    public Flux<DebitCardTransaction> findAmountCreditsPaidDebitCard(@PathVariable String id) {
        return debitCardTransactionService.findAmountCreditsPaidDebitCard(id);
    }
    
    @GetMapping("/findByCreditCreditCardCustomerId/{idcustomer}")
    public Flux<DebitCardTransaction> findByCreditCreditCardCustomerId(@PathVariable String idcustomer) {
        return debitCardTransactionService.findByCreditCreditCardCustomerId(idcustomer);
    }
    
}
