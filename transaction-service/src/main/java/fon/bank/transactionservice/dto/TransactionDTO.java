package fon.bank.transactionservice.dto;

import fon.bank.transactionservice.entity.TransactionPK;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransactionDTO {

//    private Integer transactionid;
//
//    private String sender;
//
//    private String receiver;
    private TransactionPK transactionPK;

    private Double amount;

    private LocalDateTime date;

    private String description;

    private String status;

    private String model;

    private String number;

}
