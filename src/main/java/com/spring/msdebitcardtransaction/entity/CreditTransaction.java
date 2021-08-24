package com.spring.msdebitcardtransaction.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CreditTransaction {
    private String id;

    private Credit credit;
    
    private DebitCard debitCard;

    private String transactionCode;

    private Double transactionAmount;

    private LocalDateTime transactionDateTime;


}
