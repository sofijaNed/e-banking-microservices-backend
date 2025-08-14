package fon.bank.userservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Collection;

@Data
public class ClientDTO {
    private Long id;

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name cannot exceed 50 characters")
    private String firstname;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    private String lastname;

    @Past(message = "Birthdate must be in the past")
    private LocalDate birthdate;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "\\+?[0-9\\-\\s]{7,20}", message = "Invalid phone number format")
    private String phone;

    private String address;

    @NotBlank(message = "Username is required")
    @Size(min = 4, max = 50, message = "Username must be between 4 and 50 characters")
    private String userClient;
}
