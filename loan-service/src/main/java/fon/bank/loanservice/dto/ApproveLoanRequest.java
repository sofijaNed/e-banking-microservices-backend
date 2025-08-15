package fon.bank.loanservice.dto;

import lombok.Data;
@Data
public class ApproveLoanRequest {
    private Long employeeId;
    private String note;
}
