package fon.bank.accountservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransactionDTO {

    private TransactionPK transactionPK;

    private Double amount;

    private LocalDateTime date;

    private String description;

    private String status;

    private String model;

    private String number;

}
