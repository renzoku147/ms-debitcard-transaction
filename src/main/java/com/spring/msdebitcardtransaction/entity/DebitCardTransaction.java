package com.spring.msdebitcardtransaction.entity;

import java.time.LocalDateTime;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Document("DebitCardTransaction")
@AllArgsConstructor
@NoArgsConstructor
public class DebitCardTransaction {
    @Id
    private String id;

    private Credit credit;
    
    @NotNull
    private DebitCard debitCard;

    @NotBlank
    private String transactionCode;

    @NotNull
    private Double transactionAmount;
    
    @Valid
    @NotNull
    private TypeTransactionDebitCard typeTransactionDebitCard;

    private LocalDateTime transactionDateTime;
}
