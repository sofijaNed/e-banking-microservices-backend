package fon.bank.transactionservice.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name="transaction")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction implements Serializable {

//    @Id
//    @Column(name="transactionid")
//    private Integer transactionid;
//
//    @Column(name="sender")
//    private String sender;
//
//    @JoinColumn(name="receiver")
//    private String receiver;
    @EmbeddedId
    private TransactionPK transactionPK;

    @Column(name="amount")
    private Double amount;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "description")
    private String description;

    @Column(name = "status")
    private String status;

    @Column(name = "model")
    private String model;

    @Column(name = "number")
    private String number;

}
