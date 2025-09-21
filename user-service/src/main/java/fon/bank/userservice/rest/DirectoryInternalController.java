package fon.bank.userservice.rest;

import fon.bank.userservice.dao.ClientRepository;
import fon.bank.userservice.dao.EmployeeRepository; // ako imaš; ako nema – vidi napomenu ispod
import fon.bank.userservice.dto.EmailDTO;
import fon.bank.userservice.entity.Client;
import fon.bank.userservice.entity.Employee; // ako imaš
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/internal/users")
@RequiredArgsConstructor
public class DirectoryInternalController {

    private final ClientRepository clientRepo;
    private final EmployeeRepository employeeRepo;

    @GetMapping("/clients/{username}")
    @PreAuthorize("hasAuthority('SVC_AUTH')")
    public EmailDTO clientEmail(@PathVariable String username) {
        Client c = clientRepo.findByUserClient(username);
        if (c == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Klijent nije pronadjen");
        }
        if (c.getEmail() == null || c.getEmail().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email nije poslat");
        return new EmailDTO(c.getEmail());
    }

    @GetMapping("/employees/{username}")
    @PreAuthorize("hasAuthority('SVC_AUTH')")
    public EmailDTO employeeEmail(@PathVariable String username) {
        Employee e = employeeRepo.findByUserEmployee(username);
        if (e == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Zaposleni nije pronadjen.");
        }
        if (e.getEmail() == null || e.getEmail().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email nije poslat.");
        return new EmailDTO(e.getEmail());
    }
}
