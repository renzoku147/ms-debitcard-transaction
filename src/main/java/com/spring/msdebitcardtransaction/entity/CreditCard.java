package com.spring.msdebitcardtransaction.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CreditCard implements Card{
    private String id;

    private String cardNumber;

    private Customer customer;

    private Double limitCredit;

    private LocalDate expirationDate;

    private LocalDateTime date;
}
