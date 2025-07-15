package fon.bank.userservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class EmployeeDTO {

    private Integer id;

    private String firstname;

    private String lastname;

    private String userEmployee;
}
