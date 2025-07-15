package fon.bank.transactionservice.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionPK implements Serializable {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "transactionid")
    private Integer transactionid;

    @Column(name = "sender")
    private String sender;

    @Column(name = "receiver")
    private String receiver;
}
