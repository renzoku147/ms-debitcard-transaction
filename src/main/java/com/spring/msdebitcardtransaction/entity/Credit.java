package com.spring.msdebitcardtransaction.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Credit {
    private String id;

    private CreditCard creditCard;

    private Double amount;
    
    private Integer numberQuota;

    private LocalDateTime date;

}
