package fon.bank.userservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class EmployeeDTO {

    private Long id;

    private String firstname;

    private String lastname;

    private String email;

    private String phone;

    private String position;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String userEmployee;
}
