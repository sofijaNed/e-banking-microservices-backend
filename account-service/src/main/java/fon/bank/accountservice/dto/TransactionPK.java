package fon.bank.accountservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionPK implements Serializable {

    private Integer transactionid;

    private String sender;

    private String receiver;
}
